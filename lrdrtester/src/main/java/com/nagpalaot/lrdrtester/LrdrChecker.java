package com.nagpalaot.lrdrtester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagpalaot.ecdra.lrdr.LrdrExtract;
import com.nagpalaot.ecdra.lrdr.LrdrExtractFastExcel;

public class LrdrChecker {
	private final Logger log = LoggerFactory.getLogger(LrdrChecker.class);
	
	public void processFile(File file) {
		boolean success = true;
		LrdrExtract lrdrExtract = new LrdrExtractFastExcel();
		InputStream lrdrInput;
		try {
			log.info("Extract data from " + file.getName());
			lrdrInput = new FileInputStream(file);
			lrdrExtract.setExtractInputStream(lrdrInput);
		} catch (FileNotFoundException ex) {
			success = false;
			log.error("Did not find LRDR file.  \nStacktrace: " + ex.getMessage());
		} catch (IOException ex) {
			success = false;
			log.error("Was not able to open LRDR file.  \nStacktrace: " + ex.getMessage());
		} catch (ParseException ex) {
			success = false;
			log.error("Encountered a problem parsing the LRDR file " + file.getName() + "\n" + ex.getMessage(), ex.getErrorOffset());
		}
		lrdrExtract.getLrdrLoanRecords();
		if(success) {
			log.info("Completed extracting data from " + file.getName() + " without any structural errors.\n\n");
		} else {
			log.info("Aborted extracting data from " + file.getName() + " because of structural errors.\n\n");
		}
		
	}
		
}
