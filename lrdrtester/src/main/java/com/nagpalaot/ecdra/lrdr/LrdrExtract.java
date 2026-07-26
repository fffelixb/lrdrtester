package com.nagpalaot.ecdra.lrdr;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public abstract class LrdrExtract {

	private Lrdr lrdr;
	private List<LrdrLoanRecord> lrdrLoanRecords;

	public Lrdr getLrdr() {
		return lrdr;
	}
	
	public List<LrdrLoanRecord> getLrdrLoanRecords() {
		if(lrdrLoanRecords == null) {
			lrdrLoanRecords = new ArrayList<LrdrLoanRecord>();
		}
		return lrdrLoanRecords;
	}
	
	public void setExtractInputStream(InputStream lrdrExtract)throws IOException, ParseException{
		if(lrdrExtract == null) {
			throw new IllegalArgumentException("The LRDR extract should not be null");
		}
		try {
			lrdrLoanRecords = parseExtract(lrdrExtract);
		} catch (IOException ex) {
			String msg = "There was a problem accessing the extract file";
			throw new IOException(msg + ". " + ex.getMessage());
		} catch (ParseException ex) {
			String msg = "There was a problem parsing the extract file";
			throw new ParseException(msg + ". " + ex.getMessage(), ex.getErrorOffset());
		} finally {
			try {
				if(lrdrExtract != null) {
					lrdrExtract.close();
				}
			} catch (IOException ex) {
				String msg = "There was a problem clsing the extract inputstream";
				throw new IOException(msg + ". " + ex.getMessage());
			}
		}
		if(!lrdrLoanRecords.isEmpty()) {
			lrdr = lrdrLoanRecords.get(0).getLrdr();
		}
	}
	
	public void setExtractInputStreamJustLoans(InputStream lrdrExtract, 
			Lrdr lrdr) throws IOException, ParseException {
		if(lrdrExtract == null) {
			throw new IllegalArgumentException("The LRDR extract should not be null");
		}
		this.lrdr = lrdr;
		try {
			lrdrLoanRecords = parseExtractJustLoans(lrdrExtract, lrdr);
		} catch (IOException ex) {
			String msg = "There was a problem accessing the extract file";
			throw new IOException(msg + ". " + ex.getMessage());
		} catch (ParseException ex) {
			String msg = "There was a problem parsing the extract file";
			throw new ParseException(msg + ". " + ex.getMessage(), ex.getErrorOffset());
		} finally {
			try {
				if(lrdrExtract != null) {
					lrdrExtract.close();
				}
			} catch (IOException ex) {
				String msg = "There was a problem clsing the extract inputstream";
				throw new IOException(msg + ". " + ex.getMessage());
			}
		}
	}
	
	protected abstract List<LrdrLoanRecord> parseExtract(InputStream dataToParse) throws IOException, ParseException;
	
	protected abstract List<LrdrLoanRecord> parseExtractJustLoans(InputStream dataToParse, 
			Lrdr lrdr) throws IOException, ParseException;
	
}
