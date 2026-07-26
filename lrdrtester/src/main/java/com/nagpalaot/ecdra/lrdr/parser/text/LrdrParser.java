package com.nagpalaot.ecdra.lrdr.parser.text;

import static com.nagpalaot.ecdra.lrdr.Lrdr.THREE_YEAR_OFFICIAL;
import static com.nagpalaot.ecdra.lrdr.Lrdr.TWO_YEAR_OFFICIAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.util.TextLineParser;

public abstract class LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrParser.class);

	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	/*
	 * The LRDR column references in NSLDS design document is 1-based.
	 * The substring method in String is 0-based so the constants used here
	 * offset by 1 from the values given in NSLDS design document.
	 */
	public final static int POS_RECORD_TYPE = 20;
	public final static int POS_ORG_CODE = 21;
	public final static int POS_ORG_NAME = 143;
	public final static int POS_ORG_ADDRESS = 203;
	public final static int POS_ORG_CITY = 253;
	public final static int POS_ORG_STATE = 273;
	public final static int POS_ORG_COUNTRY = 275;
	public final static int POS_ORG_ZIP = 295;
	public final static int POS_ORG_ZIP4 = 300;
	public final static int POS_ORG_RQST_DATE = 304;
	public final static int POS_ORG_CALC_DATE = 312;
	public final static int POS_COHORT_YEAR = 320;
	public final static int POS_PROGRAM = 324;
	public final static int POS_RATE_TYPE = 331;
	public final static int POS_RATE_SUB_TYPE = 332;
    
	public final static int POS_SSN = 29;
	public final static int POS_USAGE1 = 38;
	public final static int POS_NSLDS_ID_SEQ = 39;
	public final static int POS_NSLDS_ID_SUPP = 52;
	public final static int POS_LAST_NAME = 56;
	public final static int POS_FIRST_NAME = 91;
	public final static int POS_MID_NAME = 126;
	public final static int POS_DOB = 161;
	public final static int POS_ORIG_SCHOOL = 169;
	public final static int POS_ORIG_SCHOOL_BR = 175;
	public final static int POS_CLASS_BEGIN = 178;
	public final static int POS_CLASS_END = 186;
	public final static int POS_ACAD_LEVEL = 194;
	public final static int POS_ORIG_LENDER = 195;
	public final static int POS_CURRENT_LENDER = 201;
    //POS_SERVICER not currently used, value is kept for reference
    //private final static int POS_SERVICER = 207;
	public final static int POS_LOAN_TYPE = 213;
	public final static int POS_LOAN_STATUS = 215;
	public final static int POS_LOAN_STATUS_DATE = 217;
	public final static int POS_REPAY_DATE = 225;
	public final static int POS_AMOUNT = 233;
	public final static int POS_GUARANTOR = 365;
	public final static int POS_ORIG_GUARANTOR = 239;
	public final static int POS_GUARANTY_DATE = 242;
	public final static int POS_DEFAULT_DATE = 250;
	public final static int POS_CLAIM_REASON = 258;
	public final static int POS_CONSOL_INDICATOR = 260;
	public final static int POS_CONSOL_NSLDS_ID = 261;
	public final static int POS_CONSOL_NSLDS_ID_SEQ = 274;
	public final static int POS_CONSOL_NSLDS_ID_SUPP = 270;
	public final static int POS_ENROLLMENT = 278;
	public final static int POS_ENROLLMENT_DATE = 279;
	public final static int POS_LOAN_PROGRAM = 287;
	public final static int POS_USAGE2 = 312;
    
	public final static int POS_ACTUAL_N = 29;
	public final static int POS_ACTUAL_D = 37;
	public final static int POS_REPORT_N = 45;
	public final static int POS_REPORT_D = 53;
	public final static int POS_FFEL_N = 61;
	public final static int POS_FFEL_D = 69;
	public final static int POS_DL_N = 77;
	public final static int POS_DL_D = 85;
	public final static int POS_APPEAL = 93;

	public final static int LRDR_DATE_SIZE = 8;
	public final static int LRDR_NAME_LENGTH = 35;
	public final static int NUMERATOR_LENGTH = 8;
	public final static int DENOMINATOR_LENGTH = 8;
	public final static int SSN_LENGTH = 9;
	public final static int OPEID_LENGTH = 6;
	public final static int BRANCH_LENGTH = 2;
	public final static int GACODE_LENGTH = 3;

	public List<LrdrLoanRecord> parseLrdrData(BufferedReader extract, String firstLine) throws ParseException, IOException{
		List<LrdrLoanRecord> result = performParsing(extract, firstLine);
		return result;
	}
	
	protected abstract List<LrdrLoanRecord> performParsing(BufferedReader extract, String firstLine) throws ParseException, IOException;
	
	/**
	 * 
	 * Add information to lrdr so any data is kept and then just pass it back
	 * 
	 * @param headerInfo
	 * @param lrdr
	 * @return
	 * @throws ParseException
	 */
	protected Lrdr fillInHeaderInfo(String headerInfo, Lrdr lrdr) throws ParseException{
		if(lrdr == null) {
			lrdr = new Lrdr();
		}
		
		if(log.isDebugEnabled()){
			log.debug("Read in header information");
		}
		
		lrdr.setOpeid(TextLineParser.readString(headerInfo, POS_ORG_CODE, 6));
		lrdr.setSchoolName(TextLineParser.readString(headerInfo, POS_ORG_NAME, 60));
		lrdr.setSchoolAddress(TextLineParser.readString(headerInfo, POS_ORG_ADDRESS, 50));
		lrdr.setSchoolCity(TextLineParser.readString(headerInfo, POS_ORG_CITY, 20));
		lrdr.setSchoolState(TextLineParser.readString(headerInfo, POS_ORG_STATE, 2));
		lrdr.setSchoolCountry(TextLineParser.readString(headerInfo, POS_ORG_COUNTRY, 20));
		lrdr.setSchoolZip(TextLineParser.readString(headerInfo, POS_ORG_ZIP, 5));
		lrdr.setSchoolZip4(TextLineParser.readString(headerInfo, POS_ORG_ZIP4, 4));
		
		lrdr.setRequestDate(TextLineParser.readDate(headerInfo, POS_ORG_RQST_DATE, LRDR_DATE_SIZE, df));
		
		lrdr.setRateCalcDate(TextLineParser.readDate(headerInfo, POS_ORG_CALC_DATE, LRDR_DATE_SIZE, df));
		
		lrdr.setCohortYear(TextLineParser.readString(headerInfo, POS_COHORT_YEAR, 4));
		lrdr.setRateTypeCode(TextLineParser.readString(headerInfo, POS_RATE_TYPE, 1));
		if((TWO_YEAR_OFFICIAL.equalsIgnoreCase(lrdr.getRateTypeCode()))
				|| (THREE_YEAR_OFFICIAL.equalsIgnoreCase(lrdr.getRateTypeCode()))){
			lrdr.setOfficial(true);
		}
		else{
			lrdr.setOfficial(false);
		}
		
		String lrdrProgram = TextLineParser.readString(headerInfo, POS_PROGRAM, 7);
		lrdr.setProgram(lrdrProgram);
		
		// LRDRs after 2012 will typically have direct only programs so set that as default
		lrdr.setDirectOnly(true);
		
		Pattern p1 = Pattern.compile("^FFEL");
		Pattern p2 = Pattern.compile("DL$");
		Matcher m1 = p1.matcher(lrdrProgram);
		Matcher m2 = p2.matcher(lrdrProgram);
		// if the LRDR includes both FFEL and direct loans, the program string 
		// will start with FFEL and end with DL
		if ((m1.find()) && (m2.find())){
			lrdr.setBothFfelAndDirect(true);
		}
		// if the LRDR includes only FFEL, the program string will only have FFEL
		m1.reset();
		m2.reset();
		if ((m1.find()) && !(m2.find())){
			lrdr.setFfelOnly(true);
		}
		
		/*
		 * Each line in the LRDR is supposed to be 375 characters long.
		 * Since 2011, each line is no longer filled to the full length 
		 * so if, for example, the LRDR does not have a sub-type, the line may only 
		 * be 332 characters long.
		 * To avoid String out-of-bounds exception, check the length first before 
		 * parsing for sub-type.  If the line length indicates that there may be 
		 * sub-type information, then parse for sub-type, otherwise, stop parsing 
		 * and return to caller.
		 * 
		 */
		if(headerInfo.length() > POS_RATE_SUB_TYPE){
			lrdr.setRateSubTypeCode(TextLineParser.readString(headerInfo, POS_RATE_SUB_TYPE, 1));
		}
		
		log.debug("Completed reading in header information");
		return lrdr;
	}

	/**
	 * 
	 * Add information to lrdr so any data is kept and then just pass it back
	 * 
	 * @param trailerInfo
	 * @param lrdr
	 * @return
	 * @throws ParseException
	 */
	protected Lrdr fillInTrailerInfo(String trailerInfo, Lrdr lrdr) throws ParseException{
		if(lrdr == null){
			throw new IllegalArgumentException("The extract has a trailer but no header therefore it is a corrupt file.");
		}
		if(log.isDebugEnabled()){
			log.debug("Read in trailer information for " + lrdr.getOpeid());
		}
		
		lrdr.setActualNumerator(TextLineParser.readInteger(trailerInfo, POS_ACTUAL_N, NUMERATOR_LENGTH));
		lrdr.setActualDenominator(TextLineParser.readInteger(trailerInfo, POS_ACTUAL_D, DENOMINATOR_LENGTH));
		
		lrdr.setReportedNumerator(TextLineParser.readInteger(trailerInfo, POS_REPORT_N, NUMERATOR_LENGTH));
		lrdr.setReportedDenominator(TextLineParser.readInteger(trailerInfo, POS_REPORT_D, DENOMINATOR_LENGTH));
		
		// ICCount is no longer used
		
		lrdr.setFfELProgTallyNumerator(TextLineParser.readInteger(trailerInfo, POS_FFEL_N, NUMERATOR_LENGTH));
		lrdr.setFfELProgTallyDenominator(TextLineParser.readInteger(trailerInfo, POS_FFEL_D, DENOMINATOR_LENGTH));
		
		lrdr.setDlProgTallyNumerator(TextLineParser.readInteger(trailerInfo, POS_FFEL_N, NUMERATOR_LENGTH));
		lrdr.setDlProgTallyDenominator(TextLineParser.readInteger(trailerInfo, POS_FFEL_D, DENOMINATOR_LENGTH));
		
		lrdr.setAppealedRate(TextLineParser.readBoolean(trailerInfo, POS_APPEAL, 1));
		
		log.debug("Completed reading in trailer information");
		
		return lrdr;
	}

	protected void fillInLoanInfo(String loanInfo, Lrdr lrdr, 
			List<LrdrLoanRecord> lrdrLoanRecords) throws ParseException{
		if(lrdr == null) {
			throw new IllegalArgumentException("The extract does not have a header therefore it is a corrupt file.");
		}
		LrdrLoanRecord lrdrLoanRecord = new LrdrLoanRecord();
		String ssn = TextLineParser.readString(loanInfo, POS_SSN, SSN_LENGTH);
		lrdrLoanRecord.setStudentSSN(ssn);
		
		lrdrLoanRecord.setUsage1CodeValue(loanInfo.substring(POS_USAGE1, POS_USAGE1+1));
		
		// The NSLDS Loan identifier consists of a sequence number, the loan
		// number, and a supplemental number.
		// These numbers are read in separately and concatenated.
		
		// The sequence number
		StringBuffer sb = new StringBuffer(TextLineParser.readString(loanInfo, POS_NSLDS_ID_SEQ, 4));
		
		// The value between sequence and supplemental number is just the SSN of the student,
		// since we already have that, there is no reason to parse 
		// the string again
		sb.append(ssn);
		
		// Supplemental number is concatenated to loan and sequence number
		// to create the full NSLDS Loan ID
		sb.append(TextLineParser.readString(loanInfo, POS_NSLDS_ID_SUPP, 4));
		
		lrdrLoanRecord.setNsldsLoanID(sb.toString());
		
		lrdrLoanRecord.setStudentLName(TextLineParser.readString(loanInfo, POS_LAST_NAME, LRDR_NAME_LENGTH));
		lrdrLoanRecord.setStudentFName(TextLineParser.readString(loanInfo, POS_FIRST_NAME, LRDR_NAME_LENGTH));
		lrdrLoanRecord.setStudentMName(TextLineParser.readString(loanInfo, POS_MID_NAME, LRDR_NAME_LENGTH));
		
		lrdrLoanRecord.setStudentDOB(TextLineParser.readDate(loanInfo, POS_DOB, LRDR_DATE_SIZE, df));
		
		lrdrLoanRecord.setOrigSchoolCode(TextLineParser.readString(loanInfo, POS_ORIG_SCHOOL, OPEID_LENGTH));
		lrdrLoanRecord.setOrigSchoolBranchCode(TextLineParser.readString(loanInfo, POS_ORIG_SCHOOL_BR, BRANCH_LENGTH));
		
		lrdrLoanRecord.setBeginClassDate(TextLineParser.readDate(loanInfo, POS_CLASS_BEGIN, LRDR_DATE_SIZE, df));
		lrdrLoanRecord.setEndClassDate(TextLineParser.readDate(loanInfo, POS_CLASS_END, LRDR_DATE_SIZE, df));
		
		lrdrLoanRecord.setAcademicLevel(TextLineParser.readString(loanInfo, POS_ACAD_LEVEL, 1));
		lrdrLoanRecord.setOrigLenderCode(TextLineParser.readString(loanInfo, POS_ORIG_LENDER, 6));
		lrdrLoanRecord.setCurrentLenderCode(TextLineParser.readString(loanInfo, POS_CURRENT_LENDER, 6));
		lrdrLoanRecord.setLoanTypeValue(TextLineParser.readString(loanInfo, POS_LOAN_TYPE, 2));
		lrdrLoanRecord.setLoanStatusValue(TextLineParser.readString(loanInfo, POS_LOAN_STATUS, 2));
		lrdrLoanRecord.setLoanStatusDate(TextLineParser.readDate(loanInfo, POS_LOAN_STATUS_DATE, LRDR_DATE_SIZE, df));
		lrdrLoanRecord.setRepayDate(TextLineParser.readDate(loanInfo, POS_REPAY_DATE, LRDR_DATE_SIZE, df));
		
		// loan amounts are in whole dollar amounts so set scale to null
		lrdrLoanRecord.setLoanAmount(TextLineParser.readBigDecimal(loanInfo, POS_AMOUNT, 6, null));
		
		lrdrLoanRecord.setGuarantorCode(TextLineParser.readString(loanInfo, POS_GUARANTOR, GACODE_LENGTH));
		lrdrLoanRecord.setOrigGuarantorCode(TextLineParser.readString(loanInfo, POS_ORIG_GUARANTOR, GACODE_LENGTH));
		lrdrLoanRecord.setGuarantyLoanDate(TextLineParser.readDate(loanInfo, POS_GUARANTY_DATE, LRDR_DATE_SIZE, df));
		lrdrLoanRecord.setDefaultDate(TextLineParser.readDate(loanInfo, POS_DEFAULT_DATE, LRDR_DATE_SIZE, df));
		lrdrLoanRecord.setClaimReasonCode(TextLineParser.readString(loanInfo, POS_CLAIM_REASON, 2));
		
		Integer consolValue = TextLineParser.readInteger(loanInfo, POS_CONSOL_INDICATOR, 1);
		if(consolValue != null){
			int consolIndicator = consolValue.intValue();
			switch(consolIndicator){
			case(1):{
				lrdrLoanRecord.setConsolidated(true);
				lrdrLoanRecord.setConsolidationLoan(true);
				break;
			}
			case(2):{
				lrdrLoanRecord.setConsolidated(true);
				lrdrLoanRecord.setUnderlyingLoan(true);
				break;
			}
			default:{
				lrdrLoanRecord.setConsolidated(false);
				lrdrLoanRecord.setUnderlyingLoan(false);
				log.error("An invalid consolidation indicator was found for " + lrdrLoanRecord.getNsldsLoanID());
				break;
			}
			}
		}
		else{
			lrdrLoanRecord.setConsolidated(false);
			lrdrLoanRecord.setUnderlyingLoan(false);
		}
		
		StringBuffer sb1 = new StringBuffer(TextLineParser.readString(loanInfo, POS_CONSOL_NSLDS_ID_SEQ, 4));
		sb1.append(TextLineParser.readString(loanInfo, POS_CONSOL_NSLDS_ID, SSN_LENGTH));
		sb1.append(TextLineParser.readString(loanInfo, POS_CONSOL_NSLDS_ID_SUPP, 4));
		
		lrdrLoanRecord.setConsolidationLoanNsldsId(sb1.toString());
		lrdrLoanRecord.setEnrollmentCode(TextLineParser.readString(loanInfo, POS_ENROLLMENT, 1));
		lrdrLoanRecord.setEnrollmentCodeDate(TextLineParser.readDate(loanInfo, POS_ENROLLMENT_DATE, LRDR_DATE_SIZE, df));
		lrdrLoanRecord.setProgramType(TextLineParser.readString(loanInfo, POS_LOAN_PROGRAM, 1));
		lrdrLoanRecord.setUsage2CodeValue(TextLineParser.readString(loanInfo, POS_USAGE2, 2));
		
		lrdrLoanRecord.setLrdr(lrdr);
		
		lrdrLoanRecords.add(lrdrLoanRecord);
	}

	protected LrdrParams getHeaderParams(String row, int rowIndex) throws ParseException {
		LrdrParams result = new LrdrParams();
		result.setOpeid(TextLineParser.readString(row, POS_ORG_CODE, 6));
		result.setCohortYear(TextLineParser.readString(row, POS_COHORT_YEAR, 4));
		result.setRateType(TextLineParser.readString(row, POS_RATE_TYPE, 1));
		result.setRateSubType(TextLineParser.readString(row, POS_RATE_SUB_TYPE, 1));
		return result;
	}

	protected LrdrParams getTrailerParams(String row, int rowIndex) throws ParseException {
		LrdrParams result = new LrdrParams();
		result.setOpeid(TextLineParser.readString(row, POS_ORG_CODE, 6));
		result.setCohortYear(TextLineParser.readString(row, POS_COHORT_YEAR, 4));
		return result;
	}
	
	protected class LrdrParams {
		private String opeid;
		private String cohortYear;
		private String rateType;
		private String rateSubType;
		
		public String getOpeid() {
			return this.opeid;
		}
		public String getCohortYear() {
			return this.cohortYear;
		}
		public String getRateType() {
			return this.rateType;
		}
		public void setOpeid(String opeid) {
			this.opeid = opeid;
		}
		public void setCohortYear(String cohortYear) {
			this.cohortYear = cohortYear;
		}
		public void setRateType(String rateType) {
			this.rateType = rateType;
		}
		public String getRateSubType() {
			return rateSubType;
		}
		public void setRateSubType(String rateSubType) {
			this.rateSubType = rateSubType;
		}
		public boolean allMatch(LrdrParams other) {
			boolean result = false;
			if(this.opeid.equalsIgnoreCase(other.getOpeid())) {
				if(this.cohortYear.equalsIgnoreCase(other.cohortYear)) {
					if(this.rateType.equalsIgnoreCase(other.rateType)) {
						result = true;
					}
				}
			}
			return result;
		}
		public boolean basicallyMatch(LrdrParams other) {
			boolean result = false;
			if(this.opeid.equalsIgnoreCase(other.getOpeid())) {
				if(this.cohortYear.equalsIgnoreCase(other.cohortYear)) {
					result = true;
				}
			}
			return result;
		}
	}
	
}
