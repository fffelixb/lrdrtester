package com.nagpalaot.ecdra.lrdr;

public enum LrdrRecordType {
	HEADER(1),
	DATA(2),
	TRAILER(3);
	
	private final int recordType;
	
	LrdrRecordType(int recordType){
		this.recordType = recordType;
	}
	
	public int getRecordType() {
		return recordType;
	}
	
	public static LrdrRecordType get(int i) {
		for(LrdrRecordType t:LrdrRecordType.values()) {
			if(t.recordType == i) {
				return t;
			}
		}
		return null;
	}
	
}
