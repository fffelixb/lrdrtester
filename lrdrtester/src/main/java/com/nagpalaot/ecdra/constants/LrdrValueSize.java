package com.nagpalaot.ecdra.constants;

public final class LrdrValueSize {
	
	public final static int LRDR_DATE_SIZE = 8;
	public final static int LRDR_NAME_LENGTH = 35;
	public final static int NUMERATOR_LENGTH = 8;
	public final static int DENOMINATOR_LENGTH = 8;
	public final static int SSN_LENGTH = 9;
	public final static int OPEID_LENGTH = 6;
	public final static int BRANCH_LENGTH = 2;
	public final static int GACODE_LENGTH = 3;
	public final static int USAGE1CODE_LENGTH = 1;
	public final static int USAGECODE_LENGTH = 2;
	public final static int LOANCODE_LENGTH = 2;
	public final static int YEAR_LENGTH = 4;
	
	private LrdrValueSize() {
		throw new UnsupportedOperationException("Constant/Utility class cannot be instantiated.");
	}

}
