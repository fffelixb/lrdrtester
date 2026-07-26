/**
 * 
 */
package com.nagpalaot.ecdra.constants;

/**
 * @author fernando.felixberto
 *
 */
public enum Usage1Code {

	B(1, "B", "Numerator/Denominator"),
	D(2, "D", "Denominator"),
	E(3, "E", "Eligible but not counted"),
	N(4, "N", "Not Used");
	
	private final int codeId;
	private final String code;
	private final String description;
	
	private Usage1Code(int codeId, String code, String description){
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


	public static Usage1Code get(int i) {
		for (Usage1Code t : Usage1Code.values()) {
			if(t.codeId == i)
				return t;
		}
		return null;
	}

	public static Usage1Code get(String code) {
		for (Usage1Code t : Usage1Code.values()) {
			if(t.code.equalsIgnoreCase(code))
				return t;
		}
		return null;
	}
	
}
