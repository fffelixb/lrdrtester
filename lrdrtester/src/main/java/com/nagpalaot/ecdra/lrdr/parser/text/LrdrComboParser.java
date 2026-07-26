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

public class LrdrComboParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrComboParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(BufferedReader extract, String firstLine) throws ParseException, IOException {
		String reportLine = firstLine;
		boolean primaryDone = false; 	// this is used to indicate that all the information in the primary LRDR has been read 
     	                             	// i.e. header, loans, and trailer
		boolean lrdrStarted = false; 	// this is used to indicate that a LRDR, primary school, sub-school, etc. has been started
		LrdrParams primaryLrdr = null;
		LrdrParams secondaryLrdr = null;
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		int rowIndex = 0;
		while(reportLine != null) {
			if(StringUtils.isBlank(reportLine)){
				// in case there blank lines, for example, between primary and secondary school records, just skip over
				continue;
			}
			int recordTypeValue = TextLineParser.readInteger(reportLine, POS_RECORD_TYPE, 1).intValue();
			LrdrRecordType recordType = LrdrRecordType.get(recordTypeValue);

			switch(recordType){
			case HEADER:{
				String errorMsg = "There is a header before the trailer for the previous header.  This file is corrupted.";
				if(lrdr == null) {
					lrdr = fillInHeaderInfo(reportLine, lrdr);
					primaryLrdr = this.getHeaderParams(reportLine, rowIndex);
					secondaryLrdr = this.getHeaderParams(reportLine, rowIndex);
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
					secondaryLrdr = this.getHeaderParams(reportLine, rowIndex);
					if(primaryLrdr.allMatch(secondaryLrdr)) {
						errorMsg = "A secondary school LRDR should not have the same OPEID, cohort year, and rate type as the primary";
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					if(!primaryLrdr.getRateType().equalsIgnoreCase(secondaryLrdr.getRateType())) {
						errorMsg = "A secondary school LRDR should be same rate type as primary, i.e. all draft or all official";
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
					fillInLoanInfo(reportLine, lrdr, result);
				}
				break;
			}
			case TRAILER:{
				String errorMsg = "There is a trailer without a corresponding header.  This file is corrupted.";
				if(!lrdrStarted) {
					log.error(errorMsg + " " + rowIndex);
					throw new ParseException(errorMsg, rowIndex);
				}
				if(!primaryDone) {
					LrdrParams trailerParams = this.getTrailerParams(reportLine, rowIndex);
					if(!primaryLrdr.basicallyMatch(trailerParams)) {
						errorMsg = "The trailer does not match the header " + rowIndex;
						log.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
					lrdr = fillInTrailerInfo(reportLine, lrdr);
					primaryDone = true;
					lrdrStarted = false;
				}
				else {
					LrdrParams trailerParams = this.getTrailerParams(reportLine, rowIndex);
					if(!secondaryLrdr.basicallyMatch(trailerParams)) {
						errorMsg = "The trailer does not match the header " + rowIndex;
						log.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
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
			String errorMsg = "The file does not have a complete subschool LRDR.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		return result;
	}
	
}
