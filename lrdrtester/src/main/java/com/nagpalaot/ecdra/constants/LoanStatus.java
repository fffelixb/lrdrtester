/**
 * 
 */
package com.nagpalaot.ecdra.constants;

/**
 * @author fernando.felixberto
 *
 */
public enum LoanStatus {

	AE(1, "AE", "Loan Transferred to New Holder"),
	AL(2, "AL", "Abandoned Loan"),
	BC(3, "BC", "Bankruptcy Claim, Discharged"),
	BK(4, "BK", "Bankruptcy Claim, Active"),
	CA(5, "CA", "Canceled"),
	CS(6, "CS", "Closed School Discharge"),
	DA(7, "DA", "Deferred"),
	DB(8, "DB", "Defaulted, Then Bankrupt, Active, Chapter 13"),
	DC(9, "DC", "Defaulted, Compromised"),
	DD(10, "DD", "Defaulted, Then Died"), 
	DE(11, "DE", "Death"),
	DF(12, "DF", "Defaulted, Unresolved , was DU"),
	DI(13, "DI", "Disability"),
	DK(14, "DK", "Defaulted, then Bankrupt, Discharged, Chapter 13"),
	DL(15, "DL", "Defaulted, In Litigation"),
	DN(16, "DN", "Defaulted, then paid in full by consolidation"),
	DO(17, "DO", "Defaulted, Then Bankrupt, Active, Other"),
	DP(18, "DP", "Defaulted, Paid in Full"),
	DR(19, "DR", "Defaulted loan included in a rolled-up loan"),
	DS(20, "DS", "Defaulted, Then Disabled"),
	DT(21, "DT", "Defaulted. Collection Terminated"),
	DU(22, "DU", "Defaulted, Unresolved, see DF"),
	DW(23, "DW", "Defaulted, Write-Off"),
	DX(24, "DX", "Defaulted, Six Consecutive Payments, see XD"),
	DZ(25, "DZ", "Defaulted, six consecutive payments, then missed payments"),
	FB(26, "FB", "Forbearance"),
	FC(27, "FC", "False Certification Discharge"),
	IA(28, "IA", "Loan Originated"),
	ID(29, "ID", "In School or Grace Period, see IA, IG or IM"),
	IG(30, "IG", "In Grace Period"),
	IM(31, "IM", "In Military Grace"),
	OD(32, "OD", "Defaulted, Then Bankrupt, Discharged, Other"),
	PC(33, "PC", "Paid in Full Through Consolidation Loan, see DN or PN"),
	PF(34, "PF", "Paid in Full"),
	PM(35, "PM", "Presumed paid-in-full"),
	PN(36, "PN", "Paid in Full Through Consolidation Loan"),
	RF(37, "RF", "Refinanced"),
	RH(38, "RH", "Loan Transferred by DCS to Sallie Mae"),
	RP(39, "RP", "In Repayment"),
	UA(40, "UA", "Temporarily uninsured - loan not in default"),
	UB(41, "UB", "Temporarily uninsured - loan in default"),
	UC(42, "UC", "Permanently Uninsured/ Unreinsured - loan not in default"),
	UD(43, "UD", "Permanently Uninsured/ Unreinsured"),
	UI(44, "UI", "Uninsured/ Unreinsured, see AL, UA, UB, UC or UD"),
	XD(45, "XD", "Defaulted, Six Consecutive Payments"), 
	FR(46, "FR", "Fraud"), 
	FX(47, "FX", "Fraud, Satisfied"), 
	IP(48, "IP", "In Post-Deferment Grace Period"), 
	PD(49, "PD", "Permanently Disability"), 
	PZ(50, "PZ", "PLUS Child Death"), 
	VA(51, "VA", "Disabled Veteran Discharged");
	
	private final int statusId;
	private final String code;
	private final String description;
	
	private LoanStatus(int statusId, String code, String description){
		this.statusId = statusId;
		this.code = code;
		this.description = description;
	}

	public int getStatusId() {
		return statusId;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}


	public static LoanStatus get(int i) {
		for (LoanStatus t : LoanStatus.values()) {
			if(t.statusId == i)
				return t;
		}
		return null;
	}

	public static LoanStatus get(String code) {
		for (LoanStatus t : LoanStatus.values()) {
			if(t.code.equalsIgnoreCase(code))
				return t;
		}
		return null;
	}
	
}
