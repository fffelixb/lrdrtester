package com.nagpalaot.ecdra.lrdr;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class LrdrExtractFactory {
	
	public static LrdrExtract getLrdrExtract(InputStream lrdrInputStream, String fileName ) 
			throws IOException, ParseException {
		LrdrExtract result = null;
		int dotPos = fileName.indexOf(".");
		if(dotPos != -1) {
			String suffix = fileName.substring(dotPos);
			if(suffix.contains("txt")) {
				result = new LrdrExtractText();
				result.setExtractInputStream(lrdrInputStream);
			}
			else if(suffix.contains("xls")) {
				result = new LrdrExtractFastExcel();
				result.setExtractInputStream(lrdrInputStream);
			}
		}
		return result;
	}

}