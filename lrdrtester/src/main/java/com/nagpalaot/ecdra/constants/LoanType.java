/**
 * 
 */
package com.nagpalaot.ecdra.constants;

/**
 * @author fernando.felixberto
 *
 */
public enum LoanType {

	CL(1, "CL", "FFEL Consolidation Loan"),
	DU(2, "DU", "National Defense Loan"),
	D1(3, "D1", "Direct Stafford Subsidized"),
	D2(4, "D2", "Direct Stafford Unsubsidized"),
	D4(5, "D4", "Direct PLUS"),
	D5(6, "D5", "Direct Consolidated Unsubsidized"),
	D6(7, "D6", "Direct Consolidated Subsidized"),
	D7(8, "D7", "Direct PLUS Consolidated"),
	EU(9, "EU", "Perkins Expanded Lending"),
	FI(10, "FI", "Federally Insured (FISL)"), 
	IC(11, "IC", "Income Contingent (ICL)"),
	NU(12, "NU", "NDSL"),
	PL(13, "PL", "FFEL PLUS"),
	PU(14, "PU", "Federal Perkins Loan"),
	RF(15, "RF", "FFEL Refinanced Loan"),
	SF(16, "SF", "FFEL Stafford Subsidized"),
	SL(17, "SL", "Supplemental Loan (SLS)"),
	SU(18, "SU", "FFEL Stafford Unsubsidized"),
	D0(19, "D0", "Direct Stafford Subsidized (SULA Eligible)"), 
	D9(20, "D9", "Direct Consolidation Subsidized (SULA Eligible)"), 
	D3(21, "D3", "Direct Graduate PLUS"), 
	D8(22, "D8", "TEACH Loan converted from a TEACH Grant"), 
	GB(23, "GB", "FFEL Graduate PLUS"), 
	F1(24, "F1", "Federally Insured (FISL)");
	
	private final int typeId;
	private final String code;
	private final String description;
	
	private LoanType(int typeId, String code, String description){
		this.typeId = typeId;
		this.code = code;
		this.description = description;
	}
	
	public int getTypeId() {
		return typeId;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}


	public static LoanType get(int i) {
		for (LoanType t : LoanType.values()) {
			if(t.typeId == i)
				return t;
		}
		return null;
	}

	public static LoanType get(String code) {
		for (LoanType t : LoanType.values()) {
			if(t.code.equalsIgnoreCase(code))
				return t;
		}
		return null;
	}
	
}
