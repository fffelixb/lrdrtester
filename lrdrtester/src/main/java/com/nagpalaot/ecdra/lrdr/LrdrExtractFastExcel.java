package com.nagpalaot.ecdra.lrdr;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrAverageParser;
import com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrComboParser;
import com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrJustLoansParser;
import com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrParser;
import com.nagpalaot.ecdra.lrdr.parser.fastexcel.LrdrRegularParser;

public class LrdrExtractFastExcel extends LrdrExtract {
	
	private final static Logger log = LoggerFactory.getLogger(LrdrExtractFastExcel.class);
	
	@Override
	protected List<LrdrLoanRecord> parseExtract(InputStream dataToParse) throws IOException, ParseException {
		List<LrdrLoanRecord> result = null;
		try {
			ReadableWorkbook wb = new ReadableWorkbook(dataToParse);
			
			if(wb != null) {
				log.debug("Parse workbook created from LRDR file.");
				result = parseWorkbook(wb);
			}
		} finally {
			try {
				if(dataToParse != null) {
					dataToParse.close();
				}
			} catch (IOException e) {
				log.error("Failed to close LRDR input stream \n" + e.getMessage());
			}
		}
		if(result == null) {
			result = new ArrayList<LrdrLoanRecord>();
		}
		
		// The LRDR Excel file currently does not have the same consolidation loan ID used in eCDRA 
		// so for now, it needs to be "fixed"
		result = LrdrConsolUtil.fixConsolidationLoans(result);
		return result;
	}
	
	@Override
	protected List<LrdrLoanRecord> parseExtractJustLoans(InputStream dataToParse, Lrdr lrdr) throws IOException, ParseException {
		List<LrdrLoanRecord> result = null;
		try {
			ReadableWorkbook wb = new ReadableWorkbook(dataToParse);
			
			if(wb != null) {
				result = parseWorkbookLoansOnly(wb, lrdr);
			}
		} finally {
			try {
				if(dataToParse != null) {
					dataToParse.close();
				}
			} catch (IOException e) {
				log.error("Failed to close LRDR input stream \n" + e.getMessage());
			}
		}
		if(result == null) {
			result = new ArrayList<LrdrLoanRecord>();
		}
		return result;
	}
	
	private LrdrSubType getSubType(List<Row> rows) throws IOException {
		LrdrSubType result = null;
		// Read the data as though everything is valid, allow code to fail early because of nulls or other invalid data
		int subTypeCol = 0;
		Row headingRow = rows.get(0);  // first row should be header row
		for(int i=0; i<50; i++) {
			if(headingRow.hasCell(i)) {
				if("Rate Sub-Type".equalsIgnoreCase(headingRow.getCell(i).getText())) {
					subTypeCol = i;
				}
			}
		}
		Row headerRow = rows.get(1);
		Cell cell = headerRow.getCell(subTypeCol);
		String subType = "A"; 	// assume a regular LRDR
		if(CellType.EMPTY != cell.getType()){
			subType = cell.getText();
		}
		result = LrdrSubType.get(subType);
		if(result == null) {
			result = LrdrSubType.REGULAR;
		}
		return result;
	}

	private List<LrdrLoanRecord> parseWorkbook(ReadableWorkbook wb) throws ParseException, IOException{
		List<LrdrLoanRecord> result = new ArrayList<LrdrLoanRecord>();
		
		// The workbook will only have one sheet containing the LRDR data.
		Sheet sheet = wb.getFirstSheet();
		
		List<Row> rows = sheet.read();
		LrdrSubType subType = this.getSubType(rows);
		LrdrParser parser = null;
		switch(subType) {
		case AVERAGED:{
			log.debug("LRDR identified as averaged, use a LrdrAverageParser.  LRDR SubType=" + subType.getSubType());
			parser = new LrdrAverageParser();
			break;
		}
		case COMBO:{
			log.debug("LRDR identified as combo, use a LrdrComboParser.  LRDR SubType=" + subType.getSubType());
			parser = new LrdrComboParser();
			break;
		}
		default:{
			log.debug("LRDR identified as regular, use a LrdrRegularParser.  LRDR SubType=" + subType.getSubType());
			parser = new LrdrRegularParser();
		}
		}
		result = parser.parseLrdrData(rows);

		return result;
	}
	
	private List<LrdrLoanRecord> parseWorkbookLoansOnly(ReadableWorkbook wb, Lrdr lrdr) throws ParseException, IOException{
		// The workbook will only have one sheet containing the LRDR data.
		Sheet sheet = wb.getFirstSheet();
		List<Row> rows = sheet.read();
		LrdrJustLoansParser parser = new LrdrJustLoansParser();
		List<LrdrLoanRecord> result = parser.parseLrdrLoanData(rows, lrdr);
		return result;
	}
	
}
