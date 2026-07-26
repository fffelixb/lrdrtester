package com.nagpalaot.ecdra.lrdr.parser.fastexcel;

import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.HEADING_FIRST_CRITERIA;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.HEADING_HEADER_CRITERIA;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.HEADING_LOAN_CRITERIA;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.HEADING_TRAILER_CRITERIA;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_ACAD_LEVEL;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_AMOUNT;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_CLAIM_REASON;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_CLASS_BEGIN;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_CLASS_END;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_CONSOL_INDICATOR;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_CONSOL_NSLDS_ID;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_CURRENT_LENDER;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_DEFAULT_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_DOB;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_ENROLLMENT;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_ENROLLMENT_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_FIRST_NAME;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_GUARANTOR;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_GUARANTY_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_LAST_NAME;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_LOAN_IDENTIFIER;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_LOAN_STATUS;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_LOAN_STATUS_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_LOAN_TYPE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_MID_NAME;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_ORIG_GUARANTOR;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_ORIG_LENDER;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_ORIG_SCHOOL;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_RECORD_TYPE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_REPAY_DATE;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_SSN;
import static com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser.POS_USAGE1;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.constants.Usage1Code;
import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.lrdr.LrdrRecordType;

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
	
	public List<LrdrLoanRecord> parseLrdrLoanData(List<Row> rows, Lrdr lrdr) throws ParseException, IOException{
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		int rowIndex = 0;
		LrdrRecordType recordType = null;
		for(Row row : rows) {
			Cell recordTypeCell = row.getCell(POS_RECORD_TYPE);
			if(isHeading(recordTypeCell)) {
				Cell criterionCell = row.getCell(POS_RECORD_TYPE + 2);
				recordType = findRecordType(criterionCell);
			}
			else {
				switch(recordType){
				case HEADER:{
					throw new IllegalArgumentException("This LRDR file should not have a header line.");
				}
				case DATA:{
					result.add(fillInLoanInfo(row, lrdr, rowIndex));
					break;
				}
				case TRAILER:{
					throw new IllegalArgumentException("This LRDR file should not have a trailer line.");
				}
				}
			}
			rowIndex++;
		}
		return result;
	}

	private LrdrLoanRecord fillInLoanInfo(Row row, Lrdr lrdr, int rowIndex) throws ParseException {
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
	
	private boolean isHeading(Cell cell) {
		boolean result = false;
		String value = this.readString(cell);
		if(HEADING_FIRST_CRITERIA.equalsIgnoreCase(value)) {
			result = true;
		}
		return result;
	}

	private LrdrRecordType findRecordType(Cell cell) {
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

	private String extractNSLDSId(String loanIdentifier) {
		StringBuffer sb = new StringBuffer(loanIdentifier.substring(13));
		sb.append(loanIdentifier.substring(0, 9));
		sb.append(loanIdentifier.substring(9,13));
		return sb.toString();
		
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
	private Date readDate(Cell cell, String dateLabel, int rowIndex) throws ParseException {
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
	
	private BigDecimal readBigDecimal(Cell cell, String colLabel, int rowIndex, Integer scale) throws ParseException {
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
	
	private Integer readInteger(Cell cell, String colLabel, int rowIndex) throws ParseException {
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
	
	private String readString(Cell cell){
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
	
	private String generateUsage2Code(String usageCode1) {
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
	
}
