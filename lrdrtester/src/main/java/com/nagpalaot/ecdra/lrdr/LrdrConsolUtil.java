/**
 * 
 */
package com.nagpalaot.ecdra.lrdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * This utility will be used to convert the consolidation loan identifier from Excel LRDR 
 * to the consolidation NSLDS id used in eCDRA.
 * 
 * @author fernando.felixberto
 *
 */
public class LrdrConsolUtil {
	
	private final static int ACTION_NONE = 0;	// The correct NSLDS consolidation IDs are present, no correction needed
	private final static int ACTION_REPLACE_ID = 1;	// The consolidation IDs are internal record IDs that need to be changed to NSLDS consolidation IDs
	private final static int ACTION_REFORMAT_ID = 2;	// The consolidation IDs are in the format used in Excel LRDR and need to be converted to the eCDRA legacy format

	public static List<LrdrLoanRecord> fixConsolidationLoans(List<LrdrLoanRecord> lrdrLoanRecords){
		Map<String, List<String>> consolLoans = findConsolidationLoans(lrdrLoanRecords);
		Map<String, List<String>> consolIds = findConsolidationIds(lrdrLoanRecords);
		Map<String, String> consolIdMap = new LinkedHashMap<String, String>();
		int actionToTake = determineCorrectionAction(consolIds);
		switch(actionToTake) {
		case ACTION_REPLACE_ID:{
			consolIdMap = mapConsolidationIds(consolLoans, consolIds);
			updateLrdrLoanConsolIds(lrdrLoanRecords, consolIdMap);
			break;
		}
		case ACTION_REFORMAT_ID:{
			consolIdMap = mapConsolidationIds(consolIds);
			updateLrdrLoanConsolIds(lrdrLoanRecords, consolIdMap);
			break;
		}
		}
		return lrdrLoanRecords;
	}
	
	private static Map<String, List<String>> findConsolidationLoans(List<LrdrLoanRecord> lrdrLoanRecords){
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		String ssn = "";
		List<String> consolLoans = new ArrayList<String>();
		for(LrdrLoanRecord lrdrLoanRecord : lrdrLoanRecords) {
			if(!ssn.equalsIgnoreCase(lrdrLoanRecord.getStudentSSN())) {
				if(!consolLoans.isEmpty()) {
					result.put(ssn, consolLoans);
					consolLoans = new ArrayList<String>();
				}
				ssn = lrdrLoanRecord.getStudentSSN();
			}
			if(lrdrLoanRecord.isConsolidationLoan()) {
				consolLoans.add(lrdrLoanRecord.getNsldsLoanID());
			}
		}
		// add last entries to map if any
		if(!consolLoans.isEmpty()) {
			result.put(ssn, consolLoans);
		}
		
		return result;
	}
	
	private static Map<String, List<String>> findConsolidationIds(List<LrdrLoanRecord> lrdrLoanRecords){
		Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		String ssn = "";
		List<String> consolIds = new ArrayList<String>();
		for(LrdrLoanRecord lrdrLoanRecord : lrdrLoanRecords) {
			if(!ssn.equalsIgnoreCase(lrdrLoanRecord.getStudentSSN())) {
				if(!consolIds.isEmpty()) {
					result.put(ssn, consolIds);
					consolIds = new ArrayList<String>();
				}
				ssn = lrdrLoanRecord.getStudentSSN();
				
			}
			if(lrdrLoanRecord.isUnderlyingLoan() 
					&& (StringUtils.isNotBlank(lrdrLoanRecord.getConsolidationLoanNsldsId()))) {
				consolIds.add(lrdrLoanRecord.getConsolidationLoanNsldsId());
			}
		}
		// add last entries to map if any
		if(!consolIds.isEmpty()) {
			result.put(ssn, consolIds);
		}
		
		return result;
	}
	
	private static int determineCorrectionAction(Map<String, List<String>> consolidationIds) {
		int result = ACTION_NONE;
		// only need to find other actions to take if there are actual underlying loans that were consolidated
		if(!consolidationIds.isEmpty()) {
			// get a consolidation loan ID to check
			Set<String> keySet = consolidationIds.keySet();
			// need a loan to check, just get the first SSN available
			String ssn = keySet.iterator().next();
			List<String> consolIds = consolidationIds.get(ssn);
			// need a consolidation ID to check, just get the first one for this SSN
			String consolId = consolIds.get(0);
			// Quick check
			// The consolidation ID that the application recognizes is 17 digits long so 
			// check that first
			if(consolId.length() == 17) {
				// then check if the first 9 characters are the SSN,
				// if so, then it needs to be reformatted
				if(ssn.equalsIgnoreCase(consolId.substring(0, 9))) {
					result = ACTION_REFORMAT_ID;
				}
			}
			else {
				// the consolidation ID is not one that the application recognizes 
				// it needs to be replaced
				result = ACTION_REPLACE_ID;
			}
		}
		
		return result;
	}
	
	private static Map<String, String> mapConsolidationIds(Map<String, List<String>> consolidationLoans, 
			Map<String, List<String>> consolidationIds){
		Map<String, String> result = new LinkedHashMap<String, String>();
		Set<String> keySet = consolidationIds.keySet();
		for(String ssn : keySet) {
			List<String> consolLoans = consolidationLoans.get(ssn);
			List<String> consolIds = consolidationIds.get(ssn);
			
			Collections.sort(consolIds);
			if(consolLoans != null) {
				Collections.sort(consolLoans);
				int rowCounter = 0;
				for(String consolId : consolIds) {
					if(rowCounter<consolLoans.size()) {
						result.put(consolId, consolLoans.get(rowCounter));
						rowCounter++;
					}
				}
			}
			else {
				// if there are no consolidation loans, keep the original ID from the underlying loans
				for(String consolId : consolIds) {
					result.put(consolId, consolId);
				}
			}
		}
		return result;
	}
	
	private static Map<String, String> mapConsolidationIds(Map<String, List<String>> consolidationIds){
		Map<String, String> result = new LinkedHashMap<String, String>();
		Set<String> keySet = consolidationIds.keySet();
		for(String ssn : keySet) {
			List<String> consolIds = consolidationIds.get(ssn);
			for(String consolId : consolIds) {
				StringBuffer sb = new StringBuffer(consolId.substring(13));
				sb.append(consolId.substring(0, 9));
				sb.append(consolId.substring(9,13));
				result.put(consolId, sb.toString());
			}
		}
		return result;
	}
	
	private static List<LrdrLoanRecord> updateLrdrLoanConsolIds(List<LrdrLoanRecord> lrdrLoanRecords, 
			Map<String, String> consolIdMap){
		for(LrdrLoanRecord lrdrLoanRecord : lrdrLoanRecords) {
			if(lrdrLoanRecord.isUnderlyingLoan()) {
				String consolId = lrdrLoanRecord.getConsolidationLoanNsldsId();
				String correctId = consolIdMap.get(consolId);
				lrdrLoanRecord.setConsolidationLoanNsldsId(correctId);
			}
		}
		return lrdrLoanRecords;
	}
}
