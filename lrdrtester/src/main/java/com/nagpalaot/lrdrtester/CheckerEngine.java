package com.nagpalaot.lrdrtester;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckerEngine {
	private final Logger log = LoggerFactory.getLogger(CheckerEngine.class);
	
	private String fileLocation;
	private LrdrChecker lrdrChecker;

	{
		fileLocation = System.getProperty("user.home")+ File.separator + "temp";
		lrdrChecker = new LrdrChecker();
	}
	
	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	
	public void processFiles() {
		// check for files
		File directory = new File(fileLocation);
		String[] fileList = directory.list();
		log.info("Will check " + fileList.length + " LRDRs in " + fileLocation);
		// process the files
		for(int i=0; i<fileList.length; i++){
			File file = new File(fileLocation + File.separator + fileList[i]);
			lrdrChecker.processFile(file);
		}
	}
	
	
}
