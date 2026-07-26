package com.nagpalaot.ecdra.lrdr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.nagpalaot.ecdra.bo.BaseLoan;
import com.nagpalaot.ecdra.constants.LoanStatus;
import com.nagpalaot.ecdra.constants.LoanType;
import com.nagpalaot.ecdra.constants.TrueFalseFlag;
import com.nagpalaot.ecdra.constants.Usage1Code;
import com.nagpalaot.ecdra.constants.Usage2Code;

/**
 * @author fernando.felixberto
 *
 */
public class LrdrLoanRecord extends BaseLoan implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7460430332606241555L;

	private Long id;
	
	private String usage1CodeValue;

	private String usage2CodeValue;

	private String nsldsLoanId;

	private String studentSSN;

	private String studentLName;

	private String studentFName;

	private String studentMName;	// the LRDR originally used only Middle Initial but now has space for complete name

	private Date studentDOB;

	private String origSchoolCode;

	private String origSchoolBranchCode;

	private Date beginClassDate;

	private Date endClassDate;

	private String academicLevel;

	private String origLenderCode;

	private String currentLenderCode;

	private String loanTypeValue;

	private String loanStatusValue;

	private Date loanStatusDate;

	private Date repayDate;

	private BigDecimal loanAmount;

	private String guarantorCode;	// as of official cycle 2011 (2yr) 2010 (3yr) this will correspond to the value in the
									// current guarantorCode column

	private String origGuarantorCode;	// this will contain the value that formerly was used as the guarantor code

	private Date guarantyLoanDate;

	private Date defaultDate;

	private String claimReasonCode;

	private String enrollmentCode;

	private Date enrollmentCodeDate;

	private String programType;

	private BigDecimal claimAmount;

	private BigDecimal outPrincipalBalance;
	

	private String consolidationLoanNsldsId;

    /**
     * The LRDR includes a column identifying a loan as the consolidating loan.
     * This value has been recorded in the LrdrLoanRecord table and is legacy data.
     * This property is used to track that information for legacy purposes 
     * and not necessarily for current use.
     * 
     * If needed, this value may be used to override the {@link #isConsolidatingLoan()} 
     * method inherited from BaseLoan.
     * 
     */
    private String consolidationLoanFlag;
    
    /**
     * The LRDR includes a column identifying a loan as being in consolidation.
     * This value has been recorded in the LrdrLoanRecord table and is legacy data.
     * This property is used to track that information for legacy purposes 
     * and not necessarily for current use.
     * 
     * If needed, this value may be used to override the {@link #isInConsolidation()} 
     * method inherited from BaseLoan.
     * 
     */
    private String consolidatedFlag;
    
    /**
     * The LRDR includes a column identifying a loan as the underlying loan.
     * This value has been recorded in the LrdrLoanRecord table and is legacy data.
     * This property is used to track that information for legacy purposes 
     * and not necessarily for current use.
     * 
     * If needed, this value may be used to override the {@link #isUnderlyingLoan()} 
     * method inherited from BaseLoan.
     * 
     */
    private String underlyingLoanFlag;
    
	private Lrdr lrdr;
	
	public Long getId() {
		return id;
	}

	@Override
	public LoanStatus getLoanStatus() {
		LoanStatus result = LoanStatus.get(loanStatusValue);
		return result;
	}

	@Override
	public LoanType getLoanType() {
		LoanType result = LoanType.get(loanTypeValue);
		return result;
	}

	@Override
	public Usage1Code getUsage1Code() {
		Usage1Code result = Usage1Code.get(usage1CodeValue);
		return result;
	}

	@Override
	public String getGuarantorCode() {
		return guarantorCode;
	}

	@Override
	public Date getGuarantyDate() {
		return guarantyLoanDate;
	}

	@Override
	public Date getBeginDate() {
		return beginClassDate;
	}

	@Override
	public Date getEndDate() {
		return endClassDate;
	}

	@Override
	public String getNsldsId() {
		return nsldsLoanId;
	}

	@Override
	public String getConsolNsldsId() {
		return consolidationLoanNsldsId;
	}
	
	@Override
	public Date getDefaultDate() {
		return defaultDate;
	}

	public String getUsage1CodeValue() {
		return usage1CodeValue;
	}

	public void setUsage1CodeValue(String usage1CodeValue) {
		this.usage1CodeValue = usage1CodeValue;
	}

	public String getUsage2CodeValue() {
		return usage2CodeValue;
	}

	public void setUsage2CodeValue(String usage2CodeValue) {
		this.usage2CodeValue = usage2CodeValue;
	}

	public String getNsldsLoanID() {
		return nsldsLoanId;
	}

	public void setNsldsLoanID(String nsldsLoanID) {
		this.nsldsLoanId = nsldsLoanID;
	}

	public String getStudentSSN() {
		return studentSSN;
	}

	public void setStudentSSN(String studentSSN) {
		this.studentSSN = studentSSN;
	}

	public String getStudentLName() {
		return studentLName;
	}

	public void setStudentLName(String studentLName) {
		this.studentLName = studentLName;
	}

	public String getStudentFName() {
		return studentFName;
	}

	public void setStudentFName(String studentFName) {
		this.studentFName = studentFName;
	}

	public String getStudentMName() {
		return studentMName;
	}

	public void setStudentMName(String studentMName) {
		this.studentMName = studentMName;
	}

	public Date getStudentDOB() {
		return studentDOB;
	}

	public void setStudentDOB(Date studentDOB) {
		this.studentDOB = studentDOB;
	}

	public String getOrigSchoolCode() {
		return origSchoolCode;
	}

	public void setOrigSchoolCode(String origSchoolCode) {
		this.origSchoolCode = origSchoolCode;
	}

	public String getOrigSchoolBranchCode() {
		return origSchoolBranchCode;
	}

	public void setOrigSchoolBranchCode(String origSchoolBranchCode) {
		this.origSchoolBranchCode = origSchoolBranchCode;
	}

	public Date getBeginClassDate() {
		return beginClassDate;
	}

	public void setBeginClassDate(Date beginClassDate) {
		this.beginClassDate = beginClassDate;
	}

	public Date getEndClassDate() {
		return endClassDate;
	}

	public void setEndClassDate(Date endClassDate) {
		this.endClassDate = endClassDate;
	}

	public String getAcademicLevel() {
		return academicLevel;
	}

	public void setAcademicLevel(String academicLevel) {
		this.academicLevel = academicLevel;
	}

	public String getOrigLenderCode() {
		return origLenderCode;
	}

	public void setOrigLenderCode(String origLenderCode) {
		this.origLenderCode = origLenderCode;
	}

	public String getCurrentLenderCode() {
		return currentLenderCode;
	}

	public void setCurrentLenderCode(String currentLenderCode) {
		this.currentLenderCode = currentLenderCode;
	}

	public String getLoanTypeValue() {
		return loanTypeValue;
	}

	public void setLoanTypeValue(String loanTypeValue) {
		this.loanTypeValue = loanTypeValue;
	}

	public String getLoanStatusValue() {
		return loanStatusValue;
	}

	public void setLoanStatusValue(String loanStatusValue) {
		this.loanStatusValue = loanStatusValue;
	}

	public Date getLoanStatusDate() {
		return loanStatusDate;
	}

	public void setLoanStatusDate(Date loanStatusDate) {
		this.loanStatusDate = loanStatusDate;
	}

	@Override
	public Date getRepaymentDate() {
		return repayDate;
	}

	public Date getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getOrigGuarantorCode() {
		return origGuarantorCode;
	}

	public void setOrigGuarantorCode(String origGuarantorCode) {
		this.origGuarantorCode = origGuarantorCode;
	}

	public Date getGuarantyLoanDate() {
		return guarantyLoanDate;
	}

	public void setGuarantyLoanDate(Date guarantyLoanDate) {
		this.guarantyLoanDate = guarantyLoanDate;
	}

	public String getClaimReasonCode() {
		return claimReasonCode;
	}

	public void setClaimReasonCode(String claimReasonCode) {
		this.claimReasonCode = claimReasonCode;
	}

	public String getEnrollmentCode() {
		return enrollmentCode;
	}

	public void setEnrollmentCode(String enrollmentCode) {
		this.enrollmentCode = enrollmentCode;
	}

	public Date getEnrollmentCodeDate() {
		return enrollmentCodeDate;
	}

	public void setEnrollmentCodeDate(Date enrollmentCodeDate) {
		this.enrollmentCodeDate = enrollmentCodeDate;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public BigDecimal getClaimAmount() {
		return claimAmount;
	}

	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
	}

	public BigDecimal getOutPrincipalBalance() {
		return outPrincipalBalance;
	}

	public void setOutPrincipalBalance(BigDecimal outPrincipalBalance) {
		this.outPrincipalBalance = outPrincipalBalance;
	}

	/**
	 * @deprecated
	 * this is a duplicate method to {@link #getConsolidationLoanNsldsId()} 
	 * and this is not being used
	 * 
	 * @return
	 */
	public String getConsolidationLoanNSLDSID() {
		return consolidationLoanNsldsId;
	}

	/**
	 * @deprecated
	 * this is a duplicate method to {@link #setConsolidationLoanNsldsId(String)} 
	 * and this is not being used
	 * 
	 * @param consolidationLoanNSLDSID
	 */
	public void setConsolidationLoanNSLDSID(String consolidationLoanNSLDSID) {
		this.consolidationLoanNsldsId = consolidationLoanNSLDSID;
	}

	public String getConsolidationLoanFlag() {
		return consolidationLoanFlag;
	}

	public void setConsolidationLoanFlag(String consolidationLoanFlag) {
		this.consolidationLoanFlag = consolidationLoanFlag;
	}

	public String getConsolidatedFlag() {
		return consolidatedFlag;
	}

	public void setConsolidatedFlag(String consolidatedFlag) {
		this.consolidatedFlag = consolidatedFlag;
	}

	public String getUnderlyingLoanFlag() {
		return underlyingLoanFlag;
	}

	public void setUnderlyingLoanFlag(String underlyingLoanFlag) {
		this.underlyingLoanFlag = underlyingLoanFlag;
	}

	public Lrdr getLrdr() {
		return lrdr;
	}

	public void setLrdr(Lrdr lrdr) {
		this.lrdr = lrdr;
	}

	public void setGuarantorCode(String guarantorCode) {
		this.guarantorCode = guarantorCode;
	}

	public void setDefaultDate(Date defaultDate) {
		this.defaultDate = defaultDate;
	}
	
	public String getNsldsLoanId() {
		return nsldsLoanId;
	}

	public void setNsldsLoanId(String nsldsLoanId) {
		this.nsldsLoanId = nsldsLoanId;
	}

	public String getConsolidationLoanNsldsId() {
		return consolidationLoanNsldsId;
	}

	public void setConsolidationLoanNsldsId(String consolidationLoanNsldsId) {
		this.consolidationLoanNsldsId = consolidationLoanNsldsId;
	}

	public Usage2Code getUsage2Code(){
		Usage2Code result = Usage2Code.get(usage2CodeValue);
		return result;
	}
	
	public boolean isConsolidated(){
		boolean result = TrueFalseFlag.get(consolidatedFlag).isValue();
		return result;
	}
	
	public void setConsolidated(boolean consolidated){
		if(consolidated){
			consolidatedFlag = TrueFalseFlag.TRUE.getFlag();
		}
		else{
			// if not consolidated, then consolidation and underlying will always be false
			consolidatedFlag = TrueFalseFlag.FALSE.getFlag();
			consolidationLoanFlag = TrueFalseFlag.FALSE.getFlag();
			underlyingLoanFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
	public boolean isConsolidationLoan(){
		boolean result = TrueFalseFlag.get(consolidationLoanFlag).isValue();
		return result;
	}
	
	public void setConsolidationLoan(boolean consolidationLoan){
		if(consolidationLoan){
			consolidationLoanFlag = TrueFalseFlag.TRUE.getFlag();
			underlyingLoanFlag = TrueFalseFlag.FALSE.getFlag();
		}
		else{
			consolidationLoanFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
	public boolean isUnderlyingLoan(){
		boolean result = TrueFalseFlag.get(underlyingLoanFlag).isValue();
		return result;
	}
	
	public void setUnderlyingLoan(boolean underlyingLoan){
		if(underlyingLoan){
			underlyingLoanFlag = TrueFalseFlag.TRUE.getFlag();
			consolidationLoanFlag = TrueFalseFlag.FALSE.getFlag();
		}
		else{
			underlyingLoanFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
}
