package com.nagpalaot.ecdra.lrdr;

import java.io.Serializable;
import java.util.Date;

import com.nagpalaot.ecdra.constants.TrueFalseFlag;

/**
 * @author fernando.felixberto
 *
 */
public class Lrdr implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -810672017804734412L;
	
	public static final String TWO_YEAR_OFFICIAL = "A";
	public static final String TWO_YEAR_DRAFT = "D";
	public static final String THREE_YEAR_OFFICIAL = "E";
	public static final String THREE_YEAR_DRAFT = "F";
	
	private Long id;

	private String cohortYear;

	private String officialFlag;

	private String opeid;

	private String schoolName;

	private String schoolAddress;

	private String schoolCity;

	private String schoolState;

	private String schoolZip;

	private String schoolZip4;

	private String schoolCountry;

	private Date requestDate;

	private Date rateCalcDate;

	private String program;

	private Integer actualNumerator;

	private Integer actualDenominator;

	private Integer reportedNumerator;

	private Integer reportedDenominator;

	private Integer icCount;

	private Integer ffELProgTallyNumerator;

	private Integer ffELProgTallyDenominator;

	private Integer dlProgTallyNumerator;

	private Integer dlProgTallyDenominator;

	private String appealedRateFlag;

	private Integer numOfCohortYears;

	private String rateTypeCode;

	private String rateSubTypeCode;

    private String bothFFELandDirectFlag;
    
    private String directOnlyFlag;
    
    private String ffelOnlyFlag;
   
	public String getBothFFELandDirectFlag() {
		return bothFFELandDirectFlag;
	}

	public void setBothFFELandDirectFlag(String bothFFELandDirectFlag) {
		this.bothFFELandDirectFlag = bothFFELandDirectFlag;
	}

	public String getDirectOnlyFlag() {
		return directOnlyFlag;
	}

	public void setDirectOnlyFlag(String directOnlyFlag) {
		this.directOnlyFlag = directOnlyFlag;
	}

	public String getFfelOnlyFlag() {
		return ffelOnlyFlag;
	}

	public void setFfelOnlyFlag(String ffelOnlyFlag) {
		this.ffelOnlyFlag = ffelOnlyFlag;
	}

	public String getCohortYear() {
		return cohortYear;
	}

	public void setCohortYear(String cohortYear) {
		this.cohortYear = cohortYear;
	}

	public String getOfficialFlag() {
		return officialFlag;
	}

	public void setOfficialFlag(String officialFlag) {
		this.officialFlag = officialFlag;
	}

	public String getOpeid() {
		return opeid;
	}

	public void setOpeid(String opeid) {
		this.opeid = opeid;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchoolAddress() {
		return schoolAddress;
	}

	public void setSchoolAddress(String schoolAddress) {
		this.schoolAddress = schoolAddress;
	}

	public String getSchoolCity() {
		return schoolCity;
	}

	public void setSchoolCity(String schoolCity) {
		this.schoolCity = schoolCity;
	}

	public String getSchoolState() {
		return schoolState;
	}

	public void setSchoolState(String schoolState) {
		this.schoolState = schoolState;
	}

	public String getSchoolZip() {
		return schoolZip;
	}

	public void setSchoolZip(String schoolZip) {
		this.schoolZip = schoolZip;
	}

	public String getSchoolZip4() {
		return schoolZip4;
	}

	public void setSchoolZip4(String schoolZip4) {
		this.schoolZip4 = schoolZip4;
	}

	public String getSchoolCountry() {
		return schoolCountry;
	}

	public void setSchoolCountry(String schoolCountry) {
		this.schoolCountry = schoolCountry;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getRateCalcDate() {
		return rateCalcDate;
	}

	public void setRateCalcDate(Date rateCalcDate) {
		this.rateCalcDate = rateCalcDate;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public Integer getActualNumerator() {
		return actualNumerator;
	}

	public void setActualNumerator(Integer actualNumerator) {
		this.actualNumerator = actualNumerator;
	}

	public Integer getActualDenominator() {
		return actualDenominator;
	}

	public void setActualDenominator(Integer actualDenominator) {
		this.actualDenominator = actualDenominator;
	}

	public Integer getReportedNumerator() {
		return reportedNumerator;
	}

	public void setReportedNumerator(Integer reportedNumerator) {
		this.reportedNumerator = reportedNumerator;
	}

	public Integer getReportedDenominator() {
		return reportedDenominator;
	}

	public void setReportedDenominator(Integer reportedDenominator) {
		this.reportedDenominator = reportedDenominator;
	}

	public Integer getIcCount() {
		return icCount;
	}

	public void setIcCount(Integer icCount) {
		this.icCount = icCount;
	}

	public Integer getFfELProgTallyNumerator() {
		return ffELProgTallyNumerator;
	}

	public void setFfELProgTallyNumerator(Integer ffELProgTallyNumerator) {
		this.ffELProgTallyNumerator = ffELProgTallyNumerator;
	}

	public Integer getFfELProgTallyDenominator() {
		return ffELProgTallyDenominator;
	}

	public void setFfELProgTallyDenominator(Integer ffELProgTallyDenominator) {
		this.ffELProgTallyDenominator = ffELProgTallyDenominator;
	}

	public Integer getDlProgTallyNumerator() {
		return dlProgTallyNumerator;
	}

	public void setDlProgTallyNumerator(Integer dlProgTallyNumerator) {
		this.dlProgTallyNumerator = dlProgTallyNumerator;
	}

	public Integer getDlProgTallyDenominator() {
		return dlProgTallyDenominator;
	}

	public void setDlProgTallyDenominator(Integer dlProgTallyDenominator) {
		this.dlProgTallyDenominator = dlProgTallyDenominator;
	}

	public String getAppealedRateFlag() {
		return appealedRateFlag;
	}

	public void setAppealedRateFlag(String appealedRateFlag) {
		this.appealedRateFlag = appealedRateFlag;
	}

	public Integer getNumOfCohortYears() {
		return numOfCohortYears;
	}

	public void setNumOfCohortYears(Integer numOfCohortYears) {
		this.numOfCohortYears = numOfCohortYears;
	}

	public String getRateTypeCode() {
		return rateTypeCode;
	}

	public void setRateTypeCode(String rateTypeCode) {
		this.rateTypeCode = rateTypeCode;
	}

	public String getRateSubTypeCode() {
		return rateSubTypeCode;
	}

	public void setRateSubTypeCode(String rateSubTypeCode) {
		this.rateSubTypeCode = rateSubTypeCode;
	}

	public Long getId() {
		return id;
	}

	public boolean isAppealedRate(){
		boolean result = TrueFalseFlag.get(appealedRateFlag).isValue();
		return result;
	}

	public void setAppealedRate(boolean appealedRate){
		if(appealedRate){
			appealedRateFlag = TrueFalseFlag.TRUE.getFlag();
		}
		else{
			appealedRateFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
	public boolean isOfficial(){
		boolean result = TrueFalseFlag.get(officialFlag).isValue();
		return result;
	}

	public void setOfficial(boolean official){
		if(official){
			officialFlag = TrueFalseFlag.TRUE.getFlag();
		}
		else{
			officialFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
	public boolean isDirectOnly(){
		boolean result = TrueFalseFlag.get(directOnlyFlag).isValue();
		return result;
	}
	
	public void setDirectOnly(boolean directOnly){
		if(directOnly){
			directOnlyFlag = TrueFalseFlag.TRUE.getFlag();
			ffelOnlyFlag = TrueFalseFlag.FALSE.getFlag();
			bothFFELandDirectFlag = TrueFalseFlag.FALSE.getFlag();
		}
		else{
			directOnlyFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
	public boolean isFfelOnly(){
		boolean result = TrueFalseFlag.get(ffelOnlyFlag).isValue();
		return result;
	}
	
	public void setFfelOnly(boolean ffelOnly){
		if(ffelOnly){
			ffelOnlyFlag = TrueFalseFlag.TRUE.getFlag();
			directOnlyFlag = TrueFalseFlag.FALSE.getFlag();
			bothFFELandDirectFlag = TrueFalseFlag.FALSE.getFlag();
		}
		else{
			ffelOnlyFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
	public boolean isBothFfelAndDirect(){
		boolean result = TrueFalseFlag.get(bothFFELandDirectFlag).isValue();
		return result;
	}
	
	public void setBothFfelAndDirect(boolean bothFfelAndDirect){
		if(bothFfelAndDirect){
			bothFFELandDirectFlag = TrueFalseFlag.TRUE.getFlag();
			directOnlyFlag = TrueFalseFlag.FALSE.getFlag();
			ffelOnlyFlag = TrueFalseFlag.FALSE.getFlag();
		}
		else{
			bothFFELandDirectFlag = TrueFalseFlag.FALSE.getFlag();
		}
	}
	
}
