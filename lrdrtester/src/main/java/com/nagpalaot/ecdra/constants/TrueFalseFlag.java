/**
 * 
 */
package com.nagpalaot.ecdra.constants;

/**
 * The data files alternate between using T/F and Y/N to indicate true or false.
 * Sometimes, data would combine the two formats so that a flag might have T or F or Y or N 
 * values in the same file.
 * 
 * This enumeration is created to simplify the code dealing with reading true or false 
 * values from the data files.
 * 
 * @author fernando.felixberto
 *
 */
public enum TrueFalseFlag {


	TRUE("T", true),
	FALSE("F", false),
	YES("Y", true),
	NO("N", false);
	
	private final String flag;
	private final boolean value;
	
	private TrueFalseFlag(String flag, boolean value){
		this.flag = flag;
		this.value = value;
	}
	
	public String getFlag() {
		return flag;
	}

	public boolean isValue() {
		return value;
	}

	public static TrueFalseFlag get(String str) {
		for (TrueFalseFlag t : TrueFalseFlag.values()) {
			if(t.flag.equalsIgnoreCase(str))
				return t;
		}
		return null;
	}
	
}
