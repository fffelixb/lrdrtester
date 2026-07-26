package com.nagpalaot.ecdra.constants;

public final class LrdrPositionsExcel {

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
	
	private LrdrPositionsExcel() {
		throw new UnsupportedOperationException("Constant/Utility class cannot be instantiated.");
	}
}
