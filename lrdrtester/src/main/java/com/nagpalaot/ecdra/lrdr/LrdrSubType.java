package com.nagpalaot.ecdra.lrdr;

public enum LrdrSubType {

	REGULAR("A"),
	AVERAGED("B"),
	COMBO("P");
	
	private final String subType;
	
	LrdrSubType(String subType){
		this.subType = subType;
	}

	public String getSubType() {
		return subType;
	}
	
	public static LrdrSubType get(String s) {
		for(LrdrSubType t : LrdrSubType.values()) {
			if(t.subType.equalsIgnoreCase(s)) {
				return t;
			}
		}
		return null;
	}
}
