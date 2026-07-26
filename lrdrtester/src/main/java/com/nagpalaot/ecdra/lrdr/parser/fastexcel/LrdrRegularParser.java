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

public class LrdrRegularParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrRegularParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(List<Row> rows) throws ParseException, IOException {
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		// Determine the sub-type of LRDR the file contains
		// The first header will normally be on row two, column 12.  If this changes then the code will fail
		int rowIndex = 0;
		//Row row = null; 	
		boolean done = false; 	// this is used to indicate that all the information in the LRDR has been read 
		                     	// i.e. header, loans, and trailer
		LrdrRecordType recordType = null;
		LrdrParams headerParams = null;
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
				switch(recordType){
				case HEADER:{
					String errorMsg = "There should only be one header in a regular LRDR.  This indicates a corrupted file.";
					if(done) {
						errorMsg = errorMsg + "\nNew header was found after all the information in the regular LRDR has been read in.";
						errorMsg = errorMsg + "\nLRDR info: " + lrdr.getOpeid() + " - " + lrdr.getCohortYear() + " - " + lrdr.getRateTypeCode() + " - " + lrdr.getRateSubTypeCode();
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					if(lrdr != null) {
						errorMsg = errorMsg + "\nNew header was found before all the information in the regular LRDR has been read in.";
						errorMsg = errorMsg + "\nLRDR info: " + lrdr.getOpeid() + " - " + lrdr.getCohortYear() + " - " + lrdr.getRateTypeCode() + " - " + lrdr.getRateSubTypeCode();
						log.error(errorMsg + " " + rowIndex);
						throw new ParseException(errorMsg, rowIndex);
					}
					headerParams = this.getHeaderParams(row, rowIndex);
					lrdr = fillInHeaderInfo(row, lrdr, rowIndex);
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
					result.add(fillInLoanInfo(row, lrdr, rowIndex));
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
					// The trailer data row is the last entry in the spreadsheet 
					// so once it is read in, the file should be done
					LrdrParams trailerParams = this.getTrailerParams(row, rowIndex);
					if(!trailerParams.basicallyMatch(headerParams)) {
						errorMsg = "The trailer does not match the header " + rowIndex;
						log.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
					lrdr = fillInTrailerInfo(row, lrdr, rowIndex);
					// the trailer should the last line in the report
					done = true;
					if(log.isDebugEnabled()){
						log.debug("Done with trailer, extracting data from LRDR");
					}
					break;
				}
				}
			}
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
