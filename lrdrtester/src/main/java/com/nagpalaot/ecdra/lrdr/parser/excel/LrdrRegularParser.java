package com.nagpalaot.ecdra.lrdr.parser.excel;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.Lrdr;
import com.nagpalaot.ecdra.lrdr.LrdrLoanRecord;
import com.nagpalaot.ecdra.lrdr.LrdrRecordType;

public class LrdrRegularParser extends LrdrParser {

	private final static Logger log = LoggerFactory.getLogger(LrdrRegularParser.class);

	@Override
	protected List<LrdrLoanRecord> performParsing(Sheet sheet) throws ParseException, IOException {
		Lrdr lrdr = null;
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		int rowIndex = 0;
		Row row = null;
		boolean done = false;
		LrdrRecordType recordType = null;
		while(!done) {
			row = sheet.getRow(rowIndex);
			Cell recordTypeCell = row.getCell(POS_RECORD_TYPE);
			if(isHeading(recordTypeCell)) {
				Cell criterionCell = row.getCell(POS_RECORD_TYPE + 2);
				recordType = findRecordType(criterionCell);
			}
			else {
				switch(recordType){
				case HEADER:{
					lrdr = fillInHeaderInfo(row, lrdr, rowIndex);
					break;
				}
				case DATA:{
					result.add(fillInLoanInfo(row, lrdr, rowIndex));
					break;
				}
				case TRAILER:{
					// The trailer data row is the last entry in the spreadsheet 
					// so once it is read in, the file is done
					lrdr = fillInTrailerInfo(row, lrdr, rowIndex);
					// the trailer is the last line in the report
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
		
		return result;
	}

}
