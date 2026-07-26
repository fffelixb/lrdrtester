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

public class LrdrRegularParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrRegularParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(BufferedReader extract, String firstLine) throws ParseException, IOException {
		String reportLine = firstLine;
		boolean done = false;
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		LrdrParams headerParams = null;
		int rowIndex = 0;
		while(reportLine != null) {
			if(StringUtils.isBlank(reportLine)){
				throw new IllegalArgumentException("There is a blank line in the LRDR which indicates a corrupted file");
			}
			int recordTypeValue = TextLineParser.readInteger(reportLine, POS_RECORD_TYPE, 1).intValue();
			LrdrRecordType recordType = LrdrRecordType.get(recordTypeValue);
			switch(recordType){
			case HEADER:{
				String errorMsg = "There should only be one header in a regular LRDR.  This indicates a corrupted file.";
				if(done) {
					log.error(errorMsg + " " + rowIndex);
					throw new ParseException(errorMsg, rowIndex);
				}
				if(lrdr != null) {
					log.error(errorMsg + " " + rowIndex);
					throw new ParseException(errorMsg, rowIndex);
				}
				headerParams = this.getHeaderParams(reportLine, rowIndex);
				lrdr = fillInHeaderInfo(reportLine, lrdr);
				break;
			}
			case DATA:{
				String errorMsg = "This is data outside the LRDR.  This indicates a corrupted file.";
				if(done) {
					log.error(errorMsg + " " + rowIndex);
					throw new ParseException(errorMsg, rowIndex);
				}
				if(lrdr == null) {
					log.error(errorMsg + " " + rowIndex);
					throw new ParseException(errorMsg, rowIndex);
				}
				fillInLoanInfo(reportLine, lrdr, result);
				break;
			}
			case TRAILER:{
				String errorMsg = "There should only be one trailer in a regular LRDR.  This indicates a corrupted file.";
				if(done) {
					String specificMsg = "The LRDR is already complete.  " + errorMsg + " " + rowIndex;
					log.error(specificMsg + " " + rowIndex);
					throw new ParseException(specificMsg, rowIndex);
				}
				if(lrdr == null) {
					String specificMsg = "The LRDR is not yet started.  " + errorMsg + " " + rowIndex;
					log.error(specificMsg + " " + rowIndex);
					throw new ParseException(specificMsg, rowIndex);
				}
				LrdrParams trailerParams = this.getTrailerParams(reportLine, rowIndex);
				if(!trailerParams.basicallyMatch(headerParams)) {
					errorMsg = "The trailer does not match the header " + rowIndex;
					log.error(errorMsg);
					throw new IllegalArgumentException(errorMsg);
				}
				lrdr = fillInTrailerInfo(reportLine, lrdr);
				// the trailer is the last line in the report
				done = true;
				if(log.isDebugEnabled()){
					log.debug("Done with trailer, extracting data from LRDR");
				}
				break;
			}
			}
			reportLine = extract.readLine();
			rowIndex++;
		}
		if(!done) {
			String errorMsg = "The file does not have a complete LRDR.";
			log.error(errorMsg);
			throw new ParseException(errorMsg, rowIndex);
		}
		return result;
	}
	
}
