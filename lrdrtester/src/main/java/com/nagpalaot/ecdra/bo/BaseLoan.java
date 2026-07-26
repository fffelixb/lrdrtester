package com.nagpalaot.ecdra.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nagpalaot.ecdra.constants.LoanStatus;
import com.nagpalaot.ecdra.constants.LoanType;
import com.nagpalaot.ecdra.constants.Usage1Code;

/**
 * Defines the methods that can be used to uniquely identify a loan and what its 
 * effect on the borrower's usage code is.
 * 
 * Overrides equals method to allow comparison between loan records from the LRDR with 
 * loan records for different types of appeals.
 * 
 * @author fernando.felixberto
 *
 */
public abstract class BaseLoan {
	
	private final static String ZERO_NSLDS_ID = "00000000000000000";
	
	private BaseLoan consolidationLoan;
	
	public BaseLoan getConsolidationLoan() {
		return consolidationLoan;
	}

	public void setConsolidationLoan(BaseLoan consolidationLoan) {
		this.consolidationLoan = consolidationLoan;
	}
	
	public abstract Long getId();

	public abstract LoanStatus getLoanStatus();
	
	public abstract LoanType getLoanType();
	
	public abstract Usage1Code getUsage1Code();
	
	public abstract String getGuarantorCode();
	
	public abstract Date getGuarantyDate();
	
	public abstract Date getBeginDate();
	
	public abstract Date getEndDate();
	
	public abstract String getNsldsId();
	
	public abstract String getConsolNsldsId();
	
	public abstract Date getRepaymentDate();
	
	public abstract Date getDefaultDate();
	
	/**
	 * Identifies a loan as being either in default or not.
	 * 
	 * @return
	 */
	public boolean isInDefault(){
		/*
		 * 
		 * A loan will be considered in default if it has a default date unless 
		 * it has a loan status of DE or DI.
		 */
		List<LoanStatus> exceptStatuses = new ArrayList<LoanStatus>();
		exceptStatuses.add(LoanStatus.DE);
		exceptStatuses.add(LoanStatus.DI);
		boolean result = false;
		if(getDefaultDate() != null){
			result = true;
		}
		if(exceptStatuses.contains(getLoanStatus())){
			result = false;
		}
		return result;
	}
	
	/**
	 * The LRDR only identifies one countable loan per borrower, that is, a borrower will only have 
	 * 1 loan that is given a B or D code.  All other loans may have N which is not counted or E which 
	 * is eligible but not counted.
	 * 
	 * This method replaces an E code with a more appropriate one i.e. B or D based on the other properties 
	 * of the loan.  This is necessary for determining the effect of an appeal or challenge on the 
	 * loans and ultimately the borrower and the school.
	 * 
	 * @return
	 */
	public Usage1Code getCountableUsageCode(){
		Usage1Code result = getUsage1Code();
		if(result != null){
			if(result == Usage1Code.E){
				if(this.isInDefault()){
					result = Usage1Code.B;
				}
				else{
					result = Usage1Code.D;
				}
			}
		}
		else{
			// if usage1 is null, then this loan was manually added 
			// check if it is in default, if so, assume it is a B 
			// otherwise, it is a D
			// if it were N, i.e. not counted, then school would not be adding it to case
			if(this.isInDefault()){
				result = Usage1Code.B;
			}
			else{
				result = Usage1Code.D;
			}
		}
		
		return result;
	}
	
	/**
	 * Identifies a loan as being part of a consolidation of borrower loans.  A 
	 * consolidation will always involve at least two loans, one will be the 
	 * consolidating loan and the other will be the underlying loan.  There may be 
	 * more than one underlying loan for one consolidating loan.
	 * 
	 * @return
	 */
	public boolean isInConsolidation(){
		/*
		 * A loan will be considered in consolidation if a (NSLDS) consolidation loan id 
		 * is provided with the loan data.
		 */
		boolean result = false;
		if(getConsolNsldsId() != null){
			if(!getConsolNsldsId().isEmpty()){
				if(!ZERO_NSLDS_ID.equalsIgnoreCase(getConsolNsldsId())){
					result = true;
				}
			}
		}
		return result;
	}
	
	/** 
	 * Identifies a loan as being the consolidating loan in a consolidation.
	 * 
	 * @return
	 */
	public boolean isConsolidatingLoan(){
		/*
		 * The consolidating loan is identified by the (NSLDS) consolidation loan id 
		 * provided with the loan data.  In the case of the consolidating loan, this loan 
		 * id will be the same as the NSLDS loan id.
		 * 
		 * The method will check first if the loan is in consolidation otherwise the result 
		 * of comparing the NSLDS loan id and the NSLDS consolidation loan id will not 
		 * make sense.
		 * 
		 */
		if(!isInConsolidation()){
			// if not in consolidation, cannot be a consolidating loan
			return false;
		}
		boolean result = false;
		if((getNsldsId() != null) 
				&& (!getNsldsId().isEmpty())){
			if(getNsldsId().equalsIgnoreCase(getConsolNsldsId())){
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Identifies a loan as the underlying loan in a consolidation.
	 * 
	 * @return
	 */
	public boolean isUnderlyingLoan(){
		/*
		 * The consolidating loan is identified by the (NSLDS) consolidation loan id 
		 * provided with the loan data.  In the case of the underlying loan, the 
		 * NSLDS loan id will not be the same as the NSLDS consolidation loan id.
		 * 
		 * The method will check first if the loan is in consolidation otherwise the result 
		 * of comparing the NSLDS loan id and the NSLDS consolidation loan id will not 
		 * make sense.
		 * 
		 */
		if(!isInConsolidation()){
			// if not in consolidation, cannot be a underlying loan
			return false;
		}
		boolean result = false;
		if((getNsldsId() != null) 
				&& (!getNsldsId().isEmpty())){
			if(!getNsldsId().equalsIgnoreCase(getConsolNsldsId())){
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Identifies if the type of the loan is considered as a consolidation loan type
	 * 
	 * @return
	 */
	public boolean isConsolidationLoanType(){
		boolean result = false;
		switch(getLoanType()){
		case CL:
		case D5:
		case D6:
		case D7:{
			result = true;
			break;
		}
		default:{
			result = false;
		}
		}
		return result;
	}
	
	/**
	 * @return the NSLDS loan id as it appears in the LRDR position 40-56
	 * the main difference between this and the nsldsId is the character in the 
	 * 13th position, in nsldsId it is a number, in loanIdentifier it is a 
	 * letter or special character
	 */
	public String getLoanIdentifier(){
		String nsldsLoanId = getNsldsId();
		String result = null;
		if(nsldsLoanId != null){
			if(nsldsLoanId.length() == 17){
				result = nsldsLoanId.substring(0, 12);
				String specChar = nsldsLoanId.substring(12, 13);
				int specCharVal = Integer.valueOf(specChar).intValue();
				switch(specCharVal){
				case(0):{
					result = result + "{";
					break;
				}
				case(1):{
					result = result + "A";
					break;
				}
				case(2):{
					result = result + "B";
					break;
				}
				case(3):{
					result = result + "C";
					break;
				}
				case(4):{
					result = result + "D";
					break;
				}
				case(5):{
					result = result + "E";
					break;
				}
				case(6):{
					result = result + "F";
					break;
				}
				case(7):{
					result = result + "G";
					break;
				}
				case(8):{
					result = result + "H";
					break;
				}
				case(9):{
					result = result + "I";
					break;
				}
				}
				result = result + nsldsLoanId.substring(13);
			}
		}
		return result;
	}
	
	public static Usage1Code findBorrowerUsageCode(List<BaseLoan> loans){
		Usage1Code result = Usage1Code.N;
		/* if the List of loans is empty, then the borrower would not be 
		 * counted in the default rate so the usage code returned by default 
		 * is N i.e. not counted*/
		
		for(BaseLoan loan : loans){
			if(result != Usage1Code.B){
				// only need to check more loans if result is not already B,  
				// since usage code B overrides other usage codes
				if(!loan.isUnderlyingLoan()){
					// only need to check loans that are not underlying loans 
					// because underlying loans do not affect usage code for borrower
					switch(loan.getCountableUsageCode()){
					case B:{
						result = loan.getCountableUsageCode();
						break;
					}
					case D:{
						// because the code will not proceed to this point if result is already B
						// it is safe to just set the result to D
						result = loan.getCountableUsageCode();
						break;
					}
					default:{
						// only B and D need to be considered, other usage codes will 
						// just be ignored
						break;
					}
					}
				}
			}
			
		}
		return result;
	}
	

	/**
	 * In this case, equals mean they are the same loan but not necessarily 
	 * the same value.  For example, if comparing loans from different times, the 
	 * amount in the loan record may be different but if the primary identifiers 
	 * match, then the loans would be the same and so would be considered as equal.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		/*
		 * The loans will be considered equal if they have the same NSLDS loan id, 
		 * this is generated in NSLDS and provided with the LRDR.
		 * 
		 * If the NSLDS loan id is not available, such is the case with loans added manually 
		 * by the application users - specifically the school users, equality will be based 
		 * on the loans having the same:
		 * 	<li>loan type</li>
		 * 	<li>loan status</li>
		 * 	<li>begin date</li>
		 * 	<li>end date</li>
		 * 	<li>guaranty date</li>
		 * 	<li>guarantor code</li>
		 */
		BaseLoan otherLoan = (BaseLoan) obj;
		if(getNsldsId() != null){
			if(otherLoan.getNsldsId() != null){
				if(getNsldsId().equalsIgnoreCase(otherLoan.getNsldsId())){
					return true;
				}
				else{
					// if both loans have an NSLDS loan id, then they are 
					// the authoritative identifier.  if they don't match, 
					// then they are not the same loan
					return false;
				}
			}
		}
		// could not make determination based on nslds loan id 
		// it could be that one or both loans being compared have no nslds loan id
		if(getLoanType() != otherLoan.getLoanType()){
			return false;
		}
		if(getLoanStatus() != otherLoan.getLoanStatus()){
			return false;
		}
		if(getBeginDate().compareTo(otherLoan.getBeginDate()) != 0){
			return false;
		}
		if(getEndDate().compareTo(otherLoan.getEndDate()) != 0){
			return false;
		}
		if(getGuarantyDate().compareTo(otherLoan.getGuarantyDate()) != 0){
			return false;
		}
		if(getGuarantorCode() != null){
			if(getGuarantorCode().equalsIgnoreCase(otherLoan.getGuarantorCode())){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getNsldsId() == null) ? 0 : getNsldsId().hashCode());
		result = prime * result
				+ ((getLoanType() == null) ? 0 : getLoanType().hashCode());
		result = prime * result
				+ ((getLoanStatus() == null) ? 0 : getLoanStatus().hashCode());
		result = prime * result
				+ ((getBeginDate() == null) ? 0 : getBeginDate().hashCode());
		result = prime * result
				+ ((getEndDate() == null) ? 0 : getEndDate().hashCode());
		result = prime * result
				+ ((getGuarantyDate() == null) ? 0 : getGuarantyDate().hashCode());
		result = prime * result
				+ ((getGuarantorCode() == null) ? 0 : getGuarantorCode().hashCode());
		return result;
	}
	
}
