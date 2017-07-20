/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.data.ExecutionDataStore;

/**
 * @author LLT
 *
 */
public class DuplicateFilterFreqProbesAnalyzer extends FreqProbesAnalyzer {
	private Map<Integer, String> uniqueHashcodes = new HashMap<Integer, String>();

	public DuplicateFilterFreqProbesAnalyzer(ExecutionDataStore executionData, CfgCoverageBuilder coverage) {
		super(executionData, coverage);
	}
	
	@Override
	protected boolean checkIfNeededToAnalyze(int[] probes) {
		String curTestcase = coverage.getCurrentTestCase();
		int hashcode = Arrays.hashCode(probes);
		if (uniqueHashcodes.containsKey(hashcode)) {
			coverage.addDuplicate(uniqueHashcodes.get(hashcode), curTestcase);
			return false;
		} else {
			uniqueHashcodes.put(hashcode, curTestcase);
		}
		return true;
	}

}
