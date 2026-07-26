package com.nagpalaot.ecdra.lrdr.parser.text;

import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.BRANCH_LENGTH;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.GACODE_LENGTH;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.LRDR_DATE_SIZE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.LRDR_NAME_LENGTH;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.OPEID_LENGTH;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ACAD_LEVEL;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_AMOUNT;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CLAIM_REASON;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CLASS_BEGIN;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CLASS_END;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CONSOL_INDICATOR;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CONSOL_NSLDS_ID;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CONSOL_NSLDS_ID_SEQ;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CONSOL_NSLDS_ID_SUPP;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_CURRENT_LENDER;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_DEFAULT_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_DOB;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ENROLLMENT;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ENROLLMENT_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_FIRST_NAME;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_GUARANTOR;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_GUARANTY_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_LAST_NAME;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_LOAN_PROGRAM;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_LOAN_STATUS;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_LOAN_STATUS_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_LOAN_TYPE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_MID_NAME;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_NSLDS_ID_SEQ;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_NSLDS_ID_SUPP;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ORIG_GUARANTOR;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ORIG_LENDER;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ORIG_SCHOOL;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_ORIG_SCHOOL_BR;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_RECORD_TYPE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_REPAY_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_SSN;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_USAGE1;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.POS_USAGE2;
import static com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser.SSN_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.lrdr.LrdrRecordType;
import com.nagpalaot.ecdra.util.TextLineParser;

/**
 * This parser reads files that only contains loans.  This is used when a previous LRDR upload did 
 * not have a complete set of loan info.  For example, the LRDR was updated after the initial release and 
 * additional loans were included.
 * 
 * This type of upload is only performed by the FSA admin and the file is checked beforehand so the only 
 * validation done by the parser is for type correctness and typos that cause parsing exceptions.
 * 
 * @author fernando.felixberto
 *
 */
public class LrdrJustLoansParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrJustLoansParser.class);

	private DateFormat df = new SimpleDateFormat("yyyyMMdd");

	public List<LrdrLoanRecord> parseLoanData(BufferedReader extract, Lrdr lrdr) throws IOException, ParseException{
		String reportLine = null;
		boolean done = false;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		while(!done) {
			reportLine = extract.readLine();
			if(StringUtils.isBlank(reportLine)){
				throw new IllegalArgumentException("There is a blank line in the LRDR which indicates a corrupted file");
			}
			int recordTypeValue = TextLineParser.readInteger(reportLine, POS_RECORD_TYPE, 1).intValue();
			LrdrRecordType recordType = LrdrRecordType.get(recordTypeValue);

			switch(recordType){
			case HEADER:{
				throw new IllegalArgumentException("This LRDR file should not have a header line.");
			}
			case DATA:{
				fillInLoanInfo(reportLine, lrdr, result);
				break;
			}
			case TRAILER:{
				throw new IllegalArgumentException("This LRDR file should not have a trailer line.");
			}
			}
			
		}
		return result;
	}

	private void fillInLoanInfo(String loanInfo, Lrdr lrdr, 
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
	
}
