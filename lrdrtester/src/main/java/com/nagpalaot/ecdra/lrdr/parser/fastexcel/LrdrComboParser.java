package com.nagpalaot.ecdra.lrdr.parser.fastexcel;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.lrdr.LrdrRecordType;

public class LrdrComboParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrComboParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(List<Row> rows) throws ParseException, IOException {
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		int rowIndex = 0;
		int loanCount = 0;
		int loanTempCount = 0;
		boolean primaryDone = false; 	// this is used to indicate that all the information in the primary LRDR has been read 
		                             	// i.e. header, loans, and trailer
		boolean lrdrStarted = false; 	// this is used to indicate that a LRDR, primary school, sub-school, etc. has been started
		LrdrParams primaryLrdr = null;
		LrdrParams secondaryLrdr = null;
		LrdrRecordType recordType = null;
		for(Row row : rows) {
			if(!row.hasCell(0)) {
				// if the row is blank, skip to the next one
				continue;
			}
			Cell recordTypeCell = row.getCell(POS_RECORD_TYPE);
			if(isHeading(recordTypeCell)) {
				Cell criterionCell = row.getCell(POS_RECORD_TYPE + 2);
				recordType = findRecordType(criterionCell);
			}
			else {
				if(!isBlank(row.getCell(0))){
					switch(recordType){
					case HEADER:{
						String errorMsg = "There is a header before the trailer for the previous header.  This file is corrupted.";
						if(lrdr == null) {
							log.debug("Found a header row and there is no lrdr yet, this will be the header for the primary lrdr.");
							lrdr = fillInHeaderInfo(row, lrdr, rowIndex);
							primaryLrdr = this.getHeaderParams(row, rowIndex);
							secondaryLrdr = this.getHeaderParams(row, rowIndex);
							lrdrStarted = true;
						}
						else {
							log.debug("Found a header row and there is already a lrdr read in, this will be the header for a secondary lrdr.");
							if(!primaryDone) {
								log.error(errorMsg + " " + rowIndex);
								throw new ParseException(errorMsg, rowIndex);
							}
							if(lrdrStarted) {
								log.error(errorMsg + " " + rowIndex);
								throw new ParseException(errorMsg, rowIndex);
							}
							secondaryLrdr = this.getHeaderParams(row, rowIndex);
							log.debug("Secondary lrdr is for OPEID: " + secondaryLrdr.getOpeid() + " for cohort year: " + secondaryLrdr.getCohortYear() + " with rate type " + secondaryLrdr.getRateType() + " and rate subtype " + secondaryLrdr.getRateSubType());
							if(primaryLrdr.allMatch(secondaryLrdr)) {
								errorMsg = "A secondary school LRDR should not have the same OPEID, cohort year, and rate type as the primary.";
								errorMsg = errorMsg + "\nPrimary: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
								errorMsg = errorMsg + "\nSecondary: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
								log.error(errorMsg + " " + rowIndex);
								throw new ParseException(errorMsg, rowIndex);
							}
							if(!primaryLrdr.getRateType().equalsIgnoreCase(secondaryLrdr.getRateType())) {
								errorMsg = "A secondary school LRDR should be same rate type as primary, i.e. all draft or all official.";
								errorMsg = errorMsg + "\nPrimary: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
								errorMsg = errorMsg + "\nSecondary: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
								log.error(errorMsg + " " + rowIndex);
								throw new ParseException(errorMsg, rowIndex);
							}
							lrdrStarted = true;
						}
						break;
					}
					case DATA:{
						String errorMsg = "This is data outside the LRDR.  This indicates a corrupted file.";
						if(!lrdrStarted) {
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						if(primaryLrdr.getCohortYear().equalsIgnoreCase(secondaryLrdr.getCohortYear())) {
							result.add(fillInLoanInfo(row, lrdr, rowIndex));
						}
						loanCount++;
						loanTempCount++;
						break;
					}
					case TRAILER:{
						String errorMsg = "There is a trailer without a corresponding header.  This file is corrupted.";
						if(!lrdrStarted) {
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						if(!primaryDone) {
							log.debug("Found a trailer row. This should be for the primary lrdr.");
							LrdrParams trailerParams = this.getTrailerParams(row, rowIndex);
							if(!primaryLrdr.basicallyMatch(trailerParams)) {
								errorMsg = "The trailer does not match the header " + rowIndex;
								errorMsg = errorMsg + "\nHeader: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
								errorMsg = errorMsg + "\nTrailer: \t" + trailerParams.getOpeid() + " - " + trailerParams.getCohortYear() + " - " + trailerParams.getRateType() + " - " + trailerParams.getRateSubType();
								log.error(errorMsg);
								throw new IllegalArgumentException(errorMsg);
							}
							lrdr = fillInTrailerInfo(row, lrdr, rowIndex);
							primaryDone = true;
							lrdrStarted = false;
							log.debug("This should be the last record in the primary LRDR, read in " + loanTempCount + " loan records for OPEID " + trailerParams.getOpeid() + " cohort year "  + trailerParams.getCohortYear() + " with rate type " + trailerParams.getRateType() + " and rate subtype " +  trailerParams.getRateSubType());
							loanTempCount = 0;
						}
						else {
							log.debug("Found a trailer row. This should be for a secondary lrdr.");
							LrdrParams trailerParams = this.getTrailerParams(row, rowIndex);
							if(!secondaryLrdr.basicallyMatch(trailerParams)) {
								errorMsg = "The trailer does not match the header " + rowIndex;
								errorMsg = errorMsg + "\nHeader: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
								errorMsg = errorMsg + "\nTrailer: \t" + trailerParams.getOpeid() + " - " + trailerParams.getCohortYear() + " - " + trailerParams.getRateType() + " - " + trailerParams.getRateSubType();
								log.error(errorMsg);
								throw new IllegalArgumentException(errorMsg);
							}
							lrdrStarted = false;
							log.debug("This should be the last record in the secondary LRDR, read in " + loanTempCount + " loan records for OPEID " + trailerParams.getOpeid() + " cohort year "  + trailerParams.getCohortYear() + " with rate type " + trailerParams.getRateType() + " and rate subtype " +  trailerParams.getRateSubType());
							loanTempCount = 0;
						}
						break;
					}
					}
				}
				
			}
			rowIndex++;
		}
		if(!primaryDone) {
			String errorMsg = "The file does not have a complete primary school LRDR.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		if(lrdrStarted) {
			String errorMsg = "The file does not have a complete subschool LRDR.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		log.info("Completed parsing up to row " + rowIndex + " found " + loanCount + " loans and returning " + result.size() + " for the primary lrdr with OPEID " + lrdr.getOpeid() + " cohort year " + lrdr.getCohortYear() + " with rate type " + lrdr.getRateTypeCode() + " and rate subtype " + lrdr.getRateSubTypeCode());
		return result;
	}

}
