package com.nagpalaot.ecdra.lrdr.parser.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.lrdr.LrdrRecordType;
import com.nagpalaot.ecdra.util.TextLineParser;

public class LrdrAverageParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrAverageParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(BufferedReader extract, String firstLine) throws ParseException, IOException {
		String reportLine = firstLine;
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();

		int rowIndex = 0;
		boolean done = false;
		boolean primaryDone = false; 	// this is used to indicate that all the information in the primary LRDR has been read 
       	// i.e. header, loans, and trailer
		boolean lrdrStarted = false; 	// this is used to indicate that a LRDR, primary school, sub-school, etc. has been started
		boolean previousFound = false;
		boolean previous2Found = false;
		LrdrParams primaryLrdr = null;
		LrdrParams secondaryLrdr = null;
		LrdrRecordType recordType = null;
		while(reportLine != null) {
			if(StringUtils.isBlank(reportLine)){
				continue;
			}
			int recordTypeValue = TextLineParser.readInteger(reportLine, POS_RECORD_TYPE, 1).intValue();
			recordType = LrdrRecordType.get(recordTypeValue);

			switch(recordType){
			case HEADER:{
				String errorMsg = "There is a header before the trailer for the previous header.  This file is corrupted.";
				if(lrdr == null) {
					lrdr = fillInHeaderInfo(reportLine, lrdr);
					primaryLrdr = getHeaderParams(reportLine, rowIndex);
					secondaryLrdr = getHeaderParams(reportLine, rowIndex);
					lrdrStarted = true;
				}
				else {
					if(!primaryDone) {
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					if(lrdrStarted) {
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					secondaryLrdr = getHeaderParams(reportLine, rowIndex);
					done = false;
					// The averaged LRDR for draft cycle only has the current cohort year 
					// If more than one cohort year is found, throw an Exception
					if("F".equalsIgnoreCase(primaryLrdr.getRateType())) {
						errorMsg = "The draft LRDR should only have one cohort year fo data.";
						log.error(errorMsg + " " + rowIndex);
						throw new IllegalArgumentException(errorMsg);
					}
					
					// All the cohort years in the LRDR file, if more than one, should 
					// be of subtype Averaged
					if(!"B".equalsIgnoreCase(secondaryLrdr.getRateSubType())) {
						errorMsg = "Only averaged subtype should be included in the LRDR.";
						log.error(errorMsg + " " + rowIndex);
						throw new IllegalArgumentException(errorMsg);
					}
					
					// check if other schools are same OPEID and from previous years
					if(!primaryLrdr.getOpeid().equalsIgnoreCase(secondaryLrdr.getOpeid())) {
						errorMsg = "Averaged schools must all have the same OPEID.  Primary OPEID " + primaryLrdr.getOpeid() + 
								" does not match second school " + secondaryLrdr.getOpeid();
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					
					// check if data are from same cycle, i.e.g draft or official
					if(!primaryLrdr.getRateType().equalsIgnoreCase(secondaryLrdr.getRateType())) {
						errorMsg = "Averaged schools must all be from same cycle: draft or official";
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
					fillInLoanInfo(reportLine, lrdr, result);
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
					LrdrParams trailerParams = getTrailerParams(reportLine, rowIndex);
					if(!primaryLrdr.basicallyMatch(trailerParams)) {
						errorMsg = "The trailer does not match the header " + rowIndex;
						log.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
					lrdr = fillInTrailerInfo(reportLine, lrdr);
					primaryDone = true;
					done = true;
					lrdrStarted = false;
				}
				else {
					LrdrParams trailerParams = getTrailerParams(reportLine, rowIndex);
					if(!secondaryLrdr.basicallyMatch(trailerParams)) {
						errorMsg = "The trailer does not match the header " + rowIndex;
						log.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
					done = true;
					lrdrStarted = false;
				}
				break;
				}
			
			}
			reportLine = extract.readLine();
			rowIndex++;
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