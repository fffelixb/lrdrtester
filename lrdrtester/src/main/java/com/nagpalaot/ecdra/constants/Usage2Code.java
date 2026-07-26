/**
 * 
 */
package com.nagpalaot.ecdra.constants;

/**
 * @author fernando.felixberto
 *
 */
public enum Usage2Code {

	DB(1, "DB", "Direct Numerator/Denominator"),
	DD(2, "DD", "Direct Denominator"),
	E(3, "E", "Eligible but not counted"),
	FB(4, "FB", "FFEL Numerator/Denominator"),
	FD(5, "FD", "FFEL Denominator"),
	IC(6, "IC", "ICR (Negative Amortization Only)"),
	N(7, "N", "Not Used");
	
	private final int codeId;
	private final String code;
	private final String description;
	
	private Usage2Code(int codeId, String code, String description){
		this.codeId = codeId;
		this.code = code;
		this.description = description;
	}

	public int getCodeId() {
		return codeId;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}


	public static Usage2Code get(int i) {
		for (Usage2Code t : Usage2Code.values()) {
			if(t.codeId == i)
				return t;
		}
		return null;
	}

	public static Usage2Code get(String code) {
		for (Usage2Code t : Usage2Code.values()) {
			if(t.code.equalsIgnoreCase(code))
				return t;
		}
		return null;
	}
	
}
