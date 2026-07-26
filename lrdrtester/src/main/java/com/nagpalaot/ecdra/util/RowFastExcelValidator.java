package com.nagpalaot.ecdra.util;

import java.util.ArrayList;
import java.util.List;

import org.dhatim.fastexcel.reader.Row;

import com.nagpalaot.ecdra.constants.LrdrLoanPositionsExcel;
import com.nagpalaot.ecdra.constants.LrdrValueSize;

public class RowFastExcelValidator {

	public String validate(Row row) {
		List<String> errors = new ArrayList<String>();
		// check OPEID
		// check SSN
		if(!isCorrectNumberType(row.getCell(LrdrLoanPositionsExcel.POS_SSN).getText(), LrdrValueSize.SSN_LENGTH)) {
			errors.add("SSN");
		}
		// check usage1
		if(!isCorrectCodeType(row.getCell(LrdrLoanPositionsExcel.POS_USAGE1).getText(), LrdrValueSize.USAGE1CODE_LENGTH)) {
			errors.add("Usage1Code");
		}
		// check last name
		if(!isCorrectAlphaNumericType(row.getCell(LrdrLoanPositionsExcel.POS_LAST_NAME).getText(), LrdrValueSize.LRDR_NAME_LENGTH)) {
			errors.add("Last Name");
		}
		// check first name
		if(!isCorrectAlphaNumericType(row.getCell(LrdrLoanPositionsExcel.POS_FIRST_NAME).getText(), LrdrValueSize.LRDR_NAME_LENGTH)) {
			errors.add("First Name");
		}
		// check dob
		if(!isCorrectDateType(row.getCell(LrdrLoanPositionsExcel.POS_DOB).getText(), LrdrValueSize.LRDR_DATE_SIZE)) {
			errors.add("DoB");
		}
		// check begin date
		if(!isCorrectDateType(row.getCell(LrdrLoanPositionsExcel.POS_CLASS_BEGIN).getText(), LrdrValueSize.LRDR_DATE_SIZE)) {
			errors.add("Class Begin Date");
		}
		// check end date
		if(!isCorrectDateType(row.getCell(LrdrLoanPositionsExcel.POS_CLASS_END).getText(), LrdrValueSize.LRDR_DATE_SIZE)) {
			errors.add("Class End Date");
		}
		// check loan type
		if(!isCorrectCodeType(row.getCell(LrdrLoanPositionsExcel.POS_LOAN_TYPE).getText(), LrdrValueSize.LOANCODE_LENGTH)) {
			errors.add("Loan Type " + row.getCell(LrdrLoanPositionsExcel.POS_LOAN_TYPE).getText());
		}
		// check loan status
		if(!isCorrectCodeType(row.getCell(LrdrLoanPositionsExcel.POS_LOAN_STATUS).getText(), LrdrValueSize.LOANCODE_LENGTH)) {
			errors.add("Loan Status " + row.getCell(LrdrLoanPositionsExcel.POS_LOAN_STATUS).getText());
		}
		// check loan status date
		if(!isCorrectDateType(row.getCell(LrdrLoanPositionsExcel.POS_LOAN_STATUS_DATE).getText(), LrdrValueSize.LRDR_DATE_SIZE)) {
			errors.add("Loan Status Date");
		}
		// check repay date
		if(!isCorrectDateType(row.getCell(LrdrLoanPositionsExcel.POS_REPAY_DATE).getText(), LrdrValueSize.LRDR_DATE_SIZE)) {
			errors.add("Repay Date");
		}
		// check original ga code
		if(!isCorrectNumberType(row.getCell(LrdrLoanPositionsExcel.POS_ORIG_GUARANTOR).getText(), LrdrValueSize.GACODE_LENGTH)) {
			errors.add("Original Guarantor Code");
		}
		// check cohort year
		if(!isCorrectNumberType(row.getCell(LrdrLoanPositionsExcel.POS_LOAN_COHORT_YEAR).getText(), LrdrValueSize.YEAR_LENGTH)) {
			errors.add("Cohort Year");
		}
		// check current ga code
		if(!isCorrectNumberType(row.getCell(LrdrLoanPositionsExcel.POS_GUARANTOR).getText(), LrdrValueSize.GACODE_LENGTH)) {
			errors.add("Current Guarantor Code");
		}
		String result = null;
		if(!errors.isEmpty()) {
			result = errors.toString();
		}
		return result;
	}
	
	/**
	 * This verifies that a value that is supposed to be a number 
	 * is not blank (i.e. not null, not empty, not blank character) 
	 * is all digits, and is the right length.
	 * 
	 * @param valueToCheck
	 * @param length
	 * @return
	 */
	public boolean isCorrectNumberType(String valueToCheck, int length) {
		boolean result = false;
		if(!valueToCheck.isBlank()) {
			if(valueToCheck.chars().allMatch(Character::isDigit)) {
				if(valueToCheck.length() == length) {
					result = true;
				}
			}
		}
		return result;
	}
	
	/**
	 * This verifies that a value that contains alphanumeric characters 
	 * is not blank (i.e. not null, not empty, not blank character) 
	 * is all digits, and is the right exact length.
	 * 
	 * @param valueToCheck
	 * @param length
	 * @return
	 */
	public boolean isCorrectCodeType(String valueToCheck, int length) {
		boolean result = false;
		if(!valueToCheck.isBlank()) {
			if(valueToCheck.length() == length) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * This verifies that a value that contains alphanumeric characters 
	 * is not blank (i.e. not null, not empty, not blank character) 
	 * is all digits, and is the right length, i.e. less than or equal to length parameter.
	 * 
	 * @param valueToCheck
	 * @param length
	 * @return
	 */
	public boolean isCorrectAlphaNumericType(String valueToCheck, int length) {
		boolean result = false;
		if(!valueToCheck.isBlank()) {
			if(valueToCheck.length() <= length) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Verifies that a String value that is supposed to be a date 
	 * is not blank (i.e. not null, not empty, not blank character) 
	 * and is the right length.
	 * 
	 * @param valueToCheck
	 * @param length
	 * @return
	 */
	public boolean isCorrectDateType(String valueToCheck, int length) {
		boolean result = false;
		if(!valueToCheck.isBlank()) {
			if(valueToCheck.chars().allMatch(Character::isDigit)) {
				if(valueToCheck.length() == length) {
					// can add value checker here too, not right now because lazy
					result = true;
				}
			}
		}
		return result;
	}
}
