package com.nagpalaot.ecdra.lrdr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nagpalaot.ecdra.lrdr.parser.text.LrdrAverageParser;
import com.nagpalaot.ecdra.lrdr.parser.text.LrdrComboParser;
import com.nagpalaot.ecdra.lrdr.parser.text.LrdrJustLoansParser;
import com.nagpalaot.ecdra.lrdr.parser.text.LrdrParser;
import com.nagpalaot.ecdra.lrdr.parser.text.LrdrRegularParser;
import com.nagpalaot.ecdra.util.TextLineParser;

public class LrdrExtractText extends LrdrExtract {
	
	private final static int POS_RATE_SUB_TYPE = 332;
	
	private LrdrSubType getSubType(String header) throws IOException, ParseException {
		String subType = "A"; 	// assume a regular LRDR

		String subTypeVal = TextLineParser.readString(header, POS_RATE_SUB_TYPE, 1);
		if(StringUtils.isNotBlank(subTypeVal)) {
			subType = subTypeVal;
		}
		LrdrSubType result = LrdrSubType.get(subType);
		if(result == null) {
			result = LrdrSubType.REGULAR;
		}
		return result;
	}

	@Override
	protected List<LrdrLoanRecord> parseExtract(InputStream dataToParse) throws IOException, ParseException {
		BufferedReader extract = new BufferedReader(new InputStreamReader(dataToParse));
		extract.mark(0);
		String firstLine = extract.readLine();
		LrdrParser parser = null;
		LrdrSubType subType = this.getSubType(firstLine);
		switch(subType) {
		case AVERAGED:{
			parser = new LrdrAverageParser();
			break;
		}
		case COMBO:{
			parser = new LrdrComboParser();
			break;
		}
		default:{
			parser = new LrdrRegularParser();
		}
		}
		// Because the first line in the buffer has been used to determine the rate subtype, the buffered 
		// reader now starts at the second line, which does not contain the header.  So just pass the 
		// first line along with the rest of the buffered reader
		List<LrdrLoanRecord> result = parser.parseLrdrData(extract, firstLine);
		
		return result;
	}
	
	@Override
	protected List<LrdrLoanRecord> parseExtractJustLoans(InputStream dataToParse, Lrdr lrdr)
			throws IOException, ParseException {
		BufferedReader extract = new BufferedReader(new InputStreamReader(dataToParse));
		LrdrJustLoansParser parser = new LrdrJustLoansParser();
		List<LrdrLoanRecord> result = parser.parseLoanData(extract, lrdr);
		
		return result;
	}

}
