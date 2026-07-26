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

public class LrdrAverageParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrAverageParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(List<Row> rows) throws ParseException, IOException {
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();

		// Determine the sub-type of LRDR the file contains
		// The first header will normally be on row two, column 12.  If this changes then the code will fail
		int rowIndex = 0;

		boolean done = false; 	// this is used to indicate that all the information in the LRDR has been read 
		                     	// i.e. header, loans, and trailer
		boolean primaryDone = false; 	// this is used to indicate that all the information in the primary LRDR has been read 
     	                              	// i.e. header, loans, and trailer
		boolean lrdrStarted = false; 	// this is used to indicate that a LRDR, primary school, sub-school, etc. has been started
		boolean previousFound = false;
		boolean previous2Found = false;
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
				//headerCount++; //Increment header count
				Cell criterionCell = row.getCell(POS_RECORD_TYPE + 2);
				recordType = findRecordType(criterionCell);
			}
			else {
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
						log.debug("Secondary lrdr is for OPEID: " + secondaryLrdr.getOpeid() + " for cohort year: " + secondaryLrdr.getCohortYear() + " with rate type " + secondaryLrdr.getRateType() + " and rate subtype " + secondaryLrdr.getRateSubType());
						secondaryLrdr = this.getHeaderParams(row, rowIndex);
						done = false;
						// The averaged LRDR for draft cycle only has the current cohort year.
						// It is only during the official cycle that the previous and 2-year previous LRDRs 
						// are included in the file.
						// If more than one cohort year is found, throw an Exception
						if("F".equalsIgnoreCase(primaryLrdr.getRateType())) {
							errorMsg = "The draft LRDR should only have one cohort year of data.";
							errorMsg = errorMsg + "\nPrimary: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
							errorMsg = errorMsg + "\nSecondary: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						
						// All the cohort years in the LRDR file, if more than one, should 
						// be of subtype Averaged
						if(!"B".equalsIgnoreCase(secondaryLrdr.getRateSubType())) {
							errorMsg = "Only averaged subtype should be included in the LRDR.";
							errorMsg = errorMsg + "\nPrimary: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
							errorMsg = errorMsg + "\nSecondary: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						
						// check if other schools are same OPEID and from previous years
						if(!primaryLrdr.getOpeid().equalsIgnoreCase(secondaryLrdr.getOpeid())) {
							errorMsg = "Averaged schools must all have the same OPEID.  Primary OPEID " + primaryLrdr.getOpeid() + 
									" does not match second school " + secondaryLrdr.getOpeid();
							errorMsg = errorMsg + "\nPrimary: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
							errorMsg = errorMsg + "\nSecondary: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						
						// check if data are from same cycle, i.e.g draft or official
						if(!primaryLrdr.getRateType().equalsIgnoreCase(secondaryLrdr.getRateType())) {
							errorMsg = "Averaged schools must all be from same cycle: draft or official";
							errorMsg = errorMsg + "\nPrimary: \t" + primaryLrdr.getOpeid() + " - " + primaryLrdr.getCohortYear() + " - " + primaryLrdr.getRateType() + " - " + primaryLrdr.getRateSubType();
							errorMsg = errorMsg + "\nSecondary: \t" + secondaryLrdr.getOpeid() + " - " + secondaryLrdr.getCohortYear() + " - " + secondaryLrdr.getRateType() + " - " + secondaryLrdr.getRateSubType();
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						errorMsg = "Averaged schools should only have current, previous, and second previous years.  " + 
								"current year " + primaryLrdr.getCohortYear() + " other year " + secondaryLrdr.getCohortYear();
						int years = Integer.parseInt(primaryLrdr.getCohortYear()) - Integer.parseInt(secondaryLrdr.getCohortYear());
						switch(years) {
						case 1: {
							if (!previousFound){
								previousFound = true;
							}
							else {
								errorMsg = "Already have data for cohort year " + secondaryLrdr.getCohortYear() + " this LRDR is corrupted";
								log.error(errorMsg + " " + rowIndex);
								throw new ParseException(errorMsg, rowIndex);
							}
							break;
						}
						case 2:{
							if (!previous2Found){
								previous2Found = true;
							}
							else {
								errorMsg = "Already have data for cohort year " + secondaryLrdr.getCohortYear() + " this LRDR is corrupted";
								log.error(errorMsg + " " + rowIndex);
								throw new ParseException(errorMsg, rowIndex);
							}
							break;
						}
						default:{
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						}
					}
					break;
				}
				case DATA:{
					String errorMsg = "This is data outside the LRDR.  This indicates a corrupted file.";
					if(!primaryDone) {
						if(!lrdrStarted) {
							log.error(errorMsg + " " + rowIndex);
							throw new ParseException(errorMsg, rowIndex);
						}
						result.add(fillInLoanInfo(row, lrdr, rowIndex));
					}
					break;
				}
				case TRAILER:{
					String errorMsg = "There is a trailer without a corresponding header.  This file is corrupted.";
					if(!lrdrStarted && !primaryDone) {
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					if(!primaryDone) {
						LrdrParams trailerParams = this.getTrailerParams(row, rowIndex);
						if(!primaryLrdr.basicallyMatch(trailerParams)) {
							errorMsg = "The trailer does not match the header " + rowIndex;
							log.error(errorMsg);
							throw new ParseException(errorMsg, rowIndex);
						}
						lrdr = fillInTrailerInfo(row, lrdr, rowIndex);
						primaryDone = true;
						done = true;
						lrdrStarted = false;
					}
					else {
						LrdrParams trailerParams = this.getTrailerParams(row, rowIndex);
						if(!secondaryLrdr.basicallyMatch(trailerParams)) {
							errorMsg = "The trailer does not match the header " + rowIndex;
							log.error(errorMsg);
							throw new ParseException(errorMsg, rowIndex);
						}
						done = true;
						lrdrStarted = false;
					}
					break;
				}
				}
				rowIndex++;
			}

		}
		if(!primaryDone) {
			String errorMsg = "The file does not have a complete primary school LRDR.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		if(lrdrStarted) {
			String errorMsg = "The file does not have a complete previous year LRDR.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		if(previousFound ^ previous2Found) {
			String errorMsg = "The cohort years are not complete.  Previous year " + previousFound + 
					"second previous year " + previous2Found;
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		if(!done) {
			String errorMsg = "The file is missing something, probably a trailer.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		return result;

	}
}
