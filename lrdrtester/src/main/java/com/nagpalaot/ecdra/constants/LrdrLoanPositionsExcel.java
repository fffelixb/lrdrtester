package com.nagpalaot.ecdra.constants;

public final class LrdrLoanPositionsExcel {
	
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
	
	private LrdrLoanPositionsExcel() {
		throw new UnsupportedOperationException("Constant/Utility class cannot be instantiated.");
	}

}
