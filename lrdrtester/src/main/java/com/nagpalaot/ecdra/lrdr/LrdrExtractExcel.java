package com.nagpalaot.ecdra.lrdr;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.parser.excel.LrdrJustLoansParser;
import com.nagpalaot.ecdra.lrdr.parser.excel.LrdrParser;
import com.nagpalaot.ecdra.lrdr.parser.excel.LrdrRegularParser;

public class LrdrExtractExcel extends LrdrExtract {
	
	private final static Logger log = LoggerFactory.getLogger(LrdrExtractExcel.class);
	
	@Override
	protected List<LrdrLoanRecord> parseExtract(InputStream dataToParse) throws IOException, ParseException {
		List<LrdrLoanRecord> result = null;
		try {
			Workbook wb = WorkbookFactory.create(dataToParse);
			
			if(wb != null) {
				result = parseWorkbook(wb);
			}
		} catch (EncryptedDocumentException ex) {
			throw new IOException("File is encrypted /n" + ex.getMessage());
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
			Workbook wb = WorkbookFactory.create(dataToParse);
			
			if(wb != null) {
				result = parseWorkbookLoansOnly(wb, lrdr);
			}
		} catch (EncryptedDocumentException ex) {
			throw new IOException("File is encrypted /n" + ex.getMessage());
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

	private LrdrSubType getSubType(Sheet sheet) throws IOException {
		LrdrSubType result = null;
		// Read the data as though everything is valid, allow code to fail early because of nulls or other invalid data
		int subTypeCol = 0;
		Row headingRow = sheet.getRow(0); // first row should be header row
		for(int i=0; i<50; i++) {
			Cell cell = headingRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			if(cell != null) {
				if("Rate Sub-Type".equalsIgnoreCase(cell.getStringCellValue())) {
					subTypeCol = i;
				}
			}
			
		}
		Row headerRow = sheet.getRow(1);
		Cell cell = headerRow.getCell(subTypeCol);
		String subType = "A"; 	// assume a regular LRDR
		if(CellType.BLANK != cell.getCellType()){
			subType = cell.getStringCellValue();
		}
		result = LrdrSubType.get(subType);
		return result;
	}

	private List<LrdrLoanRecord> parseWorkbook(Workbook wb) throws ParseException, IOException{
		
		// The workbook will only have one sheet containing the LRDR data.
		Sheet sheet = wb.getSheetAt(0);

		LrdrSubType subType = this.getSubType(sheet);
		LrdrParser parser = null;
		switch(subType) {
		case AVERAGED:{
			break;
		}
		case COMBO:{
			break;
		}
		default:{
			parser = new LrdrRegularParser();
		}
		}
		List<LrdrLoanRecord> result = parser.parseLrdrData(sheet);
		
		return result;
	}
	
	private List<LrdrLoanRecord> parseWorkbookLoansOnly(Workbook wb, Lrdr lrdr) throws ParseException{
		
		// The workbook will only have one sheet containing the LRDR data.
		Sheet sheet = wb.getSheetAt(0);
		LrdrJustLoansParser parser = new LrdrJustLoansParser();
		List<LrdrLoanRecord> result = parser.parseLrdrLoanData(sheet, lrdr);
		return result;
	}
	
}
