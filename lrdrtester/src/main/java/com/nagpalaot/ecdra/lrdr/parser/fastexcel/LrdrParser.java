package com.nagpalaot.ecdra.lrdr.parser.fastexcel;

import static com.nagpalaot.ecdra.lrdr.Lrdr.THREE_YEAR_OFFICIAL;
import static com.nagpalaot.ecdra.lrdr.Lrdr.TWO_YEAR_OFFICIAL;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.constants.TrueFalseFlag;
import com.nagpalaot.ecdra.constants.Usage1Code;
import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.lrdr.LrdrRecordType;

public abstract class LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrParser.class);

	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	
	public final static int POS_RECORD_TYPE = 0;
	public final static int POS_ORG_CODE = 1;
	
	// header columns
	public final static int POS_ORG_NAME = 2;
	public final static int POS_ORG_ADDRESS = 3;
	public final static int POS_ORG_CITY = 4;
	public final static int POS_ORG_STATE = 5;
	public final static int POS_ORG_COUNTRY = 6;
	public final static int POS_ORG_ZIP = 7;	// zip code in Excel is all 9 digits, no hyphen
	public final static int POS_ORG_RQST_DATE = 8;
	public final static int POS_ORG_CALC_DATE = 9;
	public final static int POS_COHORT_YEAR = 10;
	public final static int POS_RATE_TYPE = 11;
	public final static int POS_RATE_SUB_TYPE = 12;
    
	// loan info columns
	public final static int POS_SSN = 2;
	public final static int POS_USAGE1 = 3;
	public final static int POS_LOAN_IDENTIFIER = 4;  	// cell contains full ID which is ssn, sequence, and ID supplement 
														// which is used in the NSLDS ID but in different order
	public final static int POS_LAST_NAME = 5;
	public final static int POS_FIRST_NAME = 6;
	public final static int POS_MID_NAME = 7;
	public final static int POS_DOB = 8;
	public final static int POS_ORIG_SCHOOL = 9;  // cell contains full 8-digit OPEID which is the 6-digit base and 2-digit branch
	// POS_ORIG_SCHOOL_HIST_INDICATOR is not currently used, value is kept here to account for jump from 9 to 11
	// public final static int POS_ORIG_SCHOOL_HIST_INDICATOR = 10; 
	public final static int POS_CLASS_BEGIN = 11;
	public final static int POS_CLASS_END = 12;
	public final static int POS_ACAD_LEVEL = 13;
	public final static int POS_ORIG_LENDER = 14;
	public final static int POS_CURRENT_LENDER = 15;
    //POS_SERVICER not currently used, value is kept for reference
    //private final static int POS_SERVICER = 16;
	public final static int POS_LOAN_TYPE = 17;
	public final static int POS_LOAN_STATUS = 18;
	public final static int POS_LOAN_STATUS_DATE = 19;
	public final static int POS_REPAY_DATE = 20;
	public final static int POS_AMOUNT = 21;
	public final static int POS_ORIG_GUARANTOR = 22;
	public final static int POS_GUARANTY_DATE = 23;	// The heading for this in the Excel file is Loan Date
	public final static int POS_DEFAULT_DATE = 24;
	public final static int POS_CLAIM_REASON = 25;
	public final static int POS_CONSOL_INDICATOR = 26;
	public final static int POS_CONSOL_NSLDS_ID = 27;  // cell contains full ID but currently this is not the same as NSLDS used by eCDRA so may not be useful
	public final static int POS_ENROLLMENT = 28;
	public final static int POS_ENROLLMENT_DATE = 29;
	// Information in cells 30, 31, 32, 33 is not currently used by eCDRA
	// public final static int POS_OUT_PRIN_BAL_REPAY = 30;
	// public final static int POS_OUT_INTEREST_REPAY = 31;
	// public final static int POS_OUT_PRIN_BAL_DEFAULT = 32;
	// public final static int POS_OUT_INTEREST_DEFAULT = 33;
	public final static int POS_LOAN_COHORT_YEAR = 34;
	//public final static int POS_AWARD_ID = 35;
	public final static int POS_GUARANTOR = 36;
    
	// trailer columns
	public final static int POS_ACTUAL_N = 2;
	public final static int POS_ACTUAL_D = 3;
	public final static int POS_LRDR_N = 4;
	public final static int POS_LRDR_D = 5;
	public final static int POS_APPEAL = 6;
	// Information in cells 30, 31, 32, 33 is not currently used by eCDRA
	// public final static int POS_TOUT_PRIN_BAL_DEFAULT = 7;
	// public final static int POS_TOUT_INTEREST_DEFAULT = 8;
	// public final static int POS_TOUT_PRIN_BAL_REPAY = 9;
	// public final static int POS_TOUT_INTEREST_REPAY = 10;
	public final static int POS_OFFICIAL_RATE = 11;
	public final static int POS_TRAILER_COHORT_YEAR = 12;

	public final static int LRDR_DATE_SIZE = 8;
	public final static int LRDR_NAME_LENGTH = 35;
	public final static int NUMERATOR_LENGTH = 8;
	public final static int DENOMINATOR_LENGTH = 8;
	public final static int SSN_LENGTH = 9;
	public final static int OPEID_LENGTH = 6;
	public final static int BRANCH_LENGTH = 2;
	public final static int GACODE_LENGTH = 3;
	
	public final static String HEADING_FIRST_CRITERIA = "Record Type"; // The value in the first column of any heading
	public final static String HEADING_HEADER_CRITERIA = "School Name"; // The value that differentiates header from loan and trailer
	public final static String HEADING_LOAN_CRITERIA = "Student SSN"; // The value that differentiates loan from header and trailer
	public final static String HEADING_TRAILER_CRITERIA = "Actual Numerator Count";  // The value that differentiates trailer from header and loan
	
	public List<LrdrLoanRecord> parseLrdrData(List<Row> rows) throws ParseException, IOException{
		List<LrdrLoanRecord> result = performParsing(rows);
		return result;
	}
	
	protected abstract List<LrdrLoanRecord> performParsing(List<Row> rows) throws ParseException, IOException;

	protected Lrdr fillInHeaderInfo(Row row, Lrdr lrdr, int rowIndex) throws ParseException {
		if(lrdr == null) {
			lrdr = new Lrdr();
		}
		if(log.isDebugEnabled()){
			log.debug("Read in header information");
		}
		
		lrdr.setOpeid(readString(row.getCell(POS_ORG_CODE)).substring(0, 6));
		lrdr.setSchoolName(readString(row.getCell(POS_ORG_NAME)));
		lrdr.setSchoolAddress(readString(row.getCell(POS_ORG_ADDRESS)));
		lrdr.setSchoolCity(readString(row.getCell(POS_ORG_CITY)));
		lrdr.setSchoolState(readString(row.getCell(POS_ORG_STATE)));
		lrdr.setSchoolCountry(readString(row.getCell(POS_ORG_COUNTRY)));
		String fullZip = readString(row.getCell(POS_ORG_ZIP));
		lrdr.setSchoolZip(fullZip.substring(0, 5));
		lrdr.setSchoolZip4(fullZip.substring(5));
		Date rqstDate = this.readDate(row.getCell(POS_ORG_RQST_DATE), "LRDR Request Date", rowIndex);
		lrdr.setRequestDate(rqstDate);

		Date calcDate = this.readDate(row.getCell(POS_ORG_CALC_DATE), "Rate Calculation Date", rowIndex);
		lrdr.setRateCalcDate(calcDate);
		
		lrdr.setCohortYear(readString(row.getCell(POS_COHORT_YEAR)));
		lrdr.setRateTypeCode(readString(row.getCell(POS_RATE_TYPE)));
		if((TWO_YEAR_OFFICIAL.equalsIgnoreCase(lrdr.getRateTypeCode()))
				|| (THREE_YEAR_OFFICIAL.equalsIgnoreCase(lrdr.getRateTypeCode()))){
			lrdr.setOfficial(true);
		}
		else{
			lrdr.setOfficial(false);
		}
		// The program is not included in the Excel LRDR, just set it to Direct Loan
		lrdr.setProgram("DL");
		
		// LRDRs after 2012 will typically have direct only programs so set that as default
		lrdr.setDirectOnly(true);
		
		lrdr.setRateSubTypeCode(readString(row.getCell(POS_RATE_SUB_TYPE)));
		
		log.debug("Completed reading in header information for OPEID: " + lrdr.getOpeid() + " for cohort year: " + lrdr.getCohortYear() + " with rate type " + lrdr.getRateTypeCode() + " and rate subtype " + lrdr.getRateSubTypeCode());
		return lrdr;
	}

	/**
	 * 
	 * Add information to lrdr so any data is kept and then just pass it back
	 * 
	 * @param row
	 * @param lrdr
	 * @param rowIndex
	 * @return
	 * @throws ParseException
	 */
	protected Lrdr fillInTrailerInfo(Row row, Lrdr lrdr, int rowIndex) throws ParseException{
		if(lrdr == null){
			throw new IllegalArgumentException("The extract has a trailer but no header therefore it is a corrupt file.");
		}
		if(log.isDebugEnabled()){
			log.debug("Read in trailer information for " + lrdr.getOpeid());
		}
		
		lrdr.setActualNumerator(readInteger(row.getCell(POS_ACTUAL_N), "Actual Numerator Count", rowIndex));
		lrdr.setActualDenominator(readInteger(row.getCell(POS_ACTUAL_D), "Acutal Denominator Count", rowIndex));
		
		lrdr.setReportedNumerator(readInteger(row.getCell(POS_LRDR_N), "Actual Numerator Count", rowIndex));
		lrdr.setReportedDenominator(readInteger(row.getCell(POS_LRDR_D), "Actual Numerator Count", rowIndex));
		
		// ICCount is no longer used
		// There are no separate FFEL and DL numerator and denominator 
		
		lrdr.setAppealedRate(readBoolean(row.getCell(POS_APPEAL), "Appealed flag", rowIndex));
		
		log.debug("Completed reading in trailer information for OPEID: " + lrdr.getOpeid() + " for cohort year: " + lrdr.getCohortYear() + " with rate type " + lrdr.getRateTypeCode() + " and rate subtype " + lrdr.getRateSubTypeCode());
		
		return lrdr;
	}

	protected LrdrLoanRecord fillInLoanInfo(Row row, Lrdr lrdr, int rowIndex) throws ParseException {
		if(lrdr == null) {
			throw new IllegalArgumentException("The extract does not have a header therefore it is a corrupt file.");
		}
		LrdrLoanRecord result = new LrdrLoanRecord();
		result.setStudentSSN(readString(row.getCell(POS_SSN)));

		// The usage1Code will be used later so save it in a variable and use that
		String usage1Code = readString(row.getCell(POS_USAGE1));
		result.setUsage1CodeValue(usage1Code);
		
		// The NSLDS Loan identifier consists of a sequence number, the loan
		// number, and a supplemental number.
		// The Excel file has these parts in a different order than the text LRDR 
		// so they need to be re-arranged
		String loanIdentifier = readString(row.getCell(POS_LOAN_IDENTIFIER));
		result.setNsldsLoanID(this.extractNSLDSId(loanIdentifier));
		
		result.setStudentLName(readString(row.getCell(POS_LAST_NAME)));
		result.setStudentFName(readString(row.getCell(POS_FIRST_NAME)));
		result.setStudentMName(readString(row.getCell(POS_MID_NAME)));
		
		result.setStudentDOB(readDate(row.getCell(POS_DOB), "DOB", rowIndex));
		
		result.setOrigSchoolCode(readString(row.getCell(POS_ORIG_SCHOOL)).substring(0, 6));
		result.setOrigSchoolBranchCode(readString(row.getCell(POS_ORIG_SCHOOL)).substring(6));
		
		result.setBeginClassDate(readDate(row.getCell(POS_CLASS_BEGIN), "Class Begin", rowIndex));
		result.setEndClassDate(readDate(row.getCell(POS_CLASS_END), "Class End", rowIndex));
		
		result.setAcademicLevel(readString(row.getCell(POS_ACAD_LEVEL)));
		result.setOrigLenderCode(readString(row.getCell(POS_ORIG_LENDER)));
		result.setCurrentLenderCode(readString(row.getCell(POS_CURRENT_LENDER)));
		result.setLoanTypeValue(readString(row.getCell(POS_LOAN_TYPE)));
		result.setLoanStatusValue(readString(row.getCell(POS_LOAN_STATUS)));
		result.setLoanStatusDate(readDate(row.getCell(POS_LOAN_STATUS_DATE), "Loan Status Date", rowIndex));
		result.setRepayDate(readDate(row.getCell(POS_REPAY_DATE), "Loan Repayment Date", rowIndex));
		
		// loan amounts are in whole dollar amounts so set scale to null
		result.setLoanAmount(readBigDecimal(row.getCell(POS_AMOUNT), "Balance Amount", rowIndex, null));
		
		result.setGuarantorCode(readString(row.getCell(POS_GUARANTOR)));
		result.setOrigGuarantorCode(readString(row.getCell(POS_ORIG_GUARANTOR)));
		result.setGuarantyLoanDate(readDate(row.getCell(POS_GUARANTY_DATE), "Loan Guaranty Date", rowIndex));
		result.setDefaultDate(readDate(row.getCell(POS_DEFAULT_DATE), "Loan Default Date", rowIndex));
		result.setClaimReasonCode(readString(row.getCell(POS_CLAIM_REASON)));
		
		Integer consolValue = readInteger(row.getCell(POS_CONSOL_INDICATOR), "Consolidation Indicator", rowIndex);
		if(consolValue != null){
			int consolIndicator = consolValue.intValue();
			switch(consolIndicator){
			case(1):{
				result.setConsolidated(true);
				result.setConsolidationLoan(true);
				break;
			}
			case(2):{
				result.setConsolidated(true);
				result.setUnderlyingLoan(true);
				break;
			}
			default:{
				result.setConsolidated(false);
				result.setUnderlyingLoan(false);
				log.error("An invalid consolidation indicator was found for " + result.getNsldsLoanID());
				break;
			}
			}
		}
		else{
			result.setConsolidated(false);
			result.setUnderlyingLoan(false);
		}
		
		result.setConsolidationLoanNsldsId(readString(row.getCell(POS_CONSOL_NSLDS_ID)));
		result.setEnrollmentCode(readString(row.getCell(POS_ENROLLMENT)));
		result.setEnrollmentCodeDate(readDate(row.getCell(POS_ENROLLMENT_DATE), "Enrollment Date", rowIndex));
		
		// The program type is not included in the Excel LRDR, FFEL is no longer active so set to Direct Loan by default
		result.setProgramType("D");
		// The usage2 code is not included in the Excel LRDR, generate based on usage1 and use that
		String usage2Code = generateUsage2Code(usage1Code);
		result.setUsage2CodeValue(usage2Code);
		
		result.setLrdr(lrdr);
		return result;
	}
	
	protected LrdrParams getHeaderParams(Row row, int rowIndex) throws ParseException {
		LrdrParams result = new LrdrParams();
		result.setOpeid(readString(row.getCell(POS_ORG_CODE)).substring(0, 6));
		result.setCohortYear(readString(row.getCell(POS_COHORT_YEAR)));
		result.setRateType(readString(row.getCell(POS_RATE_TYPE)));
		result.setRateSubType(readString(row.getCell(POS_RATE_SUB_TYPE)));
		return result;
	}

	protected LrdrParams getTrailerParams(Row row, int rowIndex) throws ParseException {
		LrdrParams result = new LrdrParams();
		String fullOpeid = readString(row.getCell(POS_ORG_CODE));
		String cohortYear = readString(row.getCell(POS_TRAILER_COHORT_YEAR));
		if(fullOpeid == null || cohortYear == null) {
			String msg = "The trailer is missing OPEID or cohort year, this is an error.  ";
			log.error(msg);
			throw new ParseException("The trailer does not have an OPEID, this is an error.  ", rowIndex); 
		}
		result.setOpeid(fullOpeid.substring(0, 6));
		result.setCohortYear(cohortYear);
		log.debug("Completed reading in trailer parameters for OPEID: " + result.getOpeid() + " for cohort year: " + result.getCohortYear());
		return result;
	}
	
	protected boolean isHeading(Cell cell) {
		boolean result = false;
		String value = this.readString(cell);
		if(HEADING_FIRST_CRITERIA.equalsIgnoreCase(value)) {
			result = true;
		}
		return result;
	}

	protected LrdrRecordType findRecordType(Cell cell) {
		int criterionValue = 0;
		String criterion = readString(cell);
		switch(criterion) {
		case HEADING_HEADER_CRITERIA:{
			criterionValue = 1;
			break;
		}
		case HEADING_LOAN_CRITERIA:{
			criterionValue = 2;
			break;
		}
		case HEADING_TRAILER_CRITERIA:{
			criterionValue = 3;
			break;
		}
		}
		LrdrRecordType result = LrdrRecordType.get(criterionValue);
		return result;
	}
	
	protected String extractNSLDSId(String loanIdentifier) {
		StringBuffer sb = new StringBuffer(loanIdentifier.substring(13));
		sb.append(loanIdentifier.substring(0, 9));
		sb.append(loanIdentifier.substring(9,13));
		return sb.toString();
		
	}
	
	protected boolean readBoolean(Cell cell, String colLabel, int rowIndex) {
		boolean result = false;
		String txtBoolean = readString(cell);
		TrueFalseFlag flagValue = TrueFalseFlag.get(txtBoolean);
		if(flagValue != null){
			result = flagValue.isValue();
		}
		else{
			log.warn("The character " + txtBoolean + " at column " + colLabel + " in row " 
					+ rowIndex + " is not a valid boolean flag, a false was returned");
		}
		return result;
	}
	
	/**
	 * LRDR dates in the Excel file are in Date format.  They are presented as an 8-digit number YYYYMMDD.
	 * Fast Excel does not have a Date type, instead dates are identified as Number so it is not 
	 * possible to check the cell type as Date.  Instead, just try to read as Date and catch 
	 * any errors and return a null if it didn't work. 
	 * 
	 * @param cell
	 * @param df
	 * @return
	 * @throws ParseException 
	 */
	protected Date readDate(Cell cell, String dateLabel, int rowIndex) throws ParseException {
		Date result = null;
		if(cell.getType() == CellType.STRING) {
			String txtDate = readString(cell);
			if(!StringUtils.isBlank(txtDate)) {
				try {
					result = df.parse(txtDate);
				} catch (ParseException ex) {
					String msg = "There was a problem converting the text " + txtDate + " for " + dateLabel + "on row " + rowIndex + " to a date";
					log.error(msg);
					throw new ParseException(ex.getMessage(), rowIndex);
				}
			}
		}
		else {
			try {
				if(cell.getRawValue() != null) {
					result = Date.from(cell.asDate().atZone(ZoneId.systemDefault()).toInstant());
				}
			} catch (Exception ex) {
				String msg = "There was a problem reading the date from cell " + dateLabel + " on row " + rowIndex;
				log.error(msg);
				throw new ParseException(ex.getMessage(), rowIndex);
			}
		}
		
		return result;
	}
	
	protected BigDecimal readBigDecimal(Cell cell, String colLabel, int rowIndex, Integer scale) throws ParseException {
		BigDecimal result = null;
		String txtDecimal = readString(cell);
		if(!StringUtils.isBlank(txtDecimal)) {
			try {
				long tempValue = Long.parseLong(txtDecimal);
				if(scale == null){
					result = BigDecimal.valueOf(tempValue);
				}
				else{
					result = BigDecimal.valueOf(tempValue, scale.intValue());
				}
			} catch (NumberFormatException ex) {
				String msg = "The value " + txtDecimal + " for " + colLabel + " in row "
						+ rowIndex + " cannot be converted into a BigDecimal";
				log.error(msg);
				throw new ParseException(ex.getMessage(), rowIndex);
			}
		}
		return result;
	}
	
	protected Integer readInteger(Cell cell, String colLabel, int rowIndex) throws ParseException {
		Integer result = null;
		String txtInteger = readString(cell);
		if(!StringUtils.isBlank(txtInteger)) {
			try {
				result = Integer.valueOf(txtInteger);
			} catch (NumberFormatException ex) {
				String msg = "The value " + txtInteger + " for " + colLabel + " in row "
						+ rowIndex + " cannot be converted into an Integer";
				log.error(msg);
				throw new ParseException(ex.getMessage(), rowIndex);
			}
		}
		return result;
	}
	
	protected String readString(Cell cell){
		String result = null;
		// check if null
		if(cell != null){
			// check if blank
			if(CellType.EMPTY != cell.getType()){
				result = cell.getText();
			}
		}
		return result;
	}
	
	protected boolean isBlank(Cell cell) {
		boolean result = false;
		if((cell == null) || (CellType.EMPTY == cell.getType())) {
			result = true;
		}
		return result;
	}
	
	protected String generateUsage2Code(String usageCode1) {
		String result = "N";
		if(Usage1Code.B.getCode().equalsIgnoreCase(usageCode1)) {
			result = "DB";
		}
		else if(Usage1Code.D.getCode().equalsIgnoreCase(usageCode1)) {
			result = "DD";
		}
		else if(Usage1Code.E.getCode().equalsIgnoreCase(usageCode1)) {
			result = "E";
		}
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
