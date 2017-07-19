/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis;

import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.data.ExecutionDataStore;

/**
 * @author LLT
 *
 */
public class DuplicateFilterFreqProbesAnalyzer extends FreqProbesAnalyzer {
	private Map<int[], String> uniqueProbes = new HashMap<int[], String>();

	public DuplicateFilterFreqProbesAnalyzer(ExecutionDataStore executionData, CfgCoverageBuilder coverage) {
		super(executionData, coverage);
	}

	@Override
	protected boolean checkIfNeededToAnalyze(int[] probes) {
		String curTestcase = coverage.getCurrentTestCase();
		if (uniqueProbes.containsKey(probes)) {
			coverage.addDuplicate(uniqueProbes.get(probes), curTestcase);
			return false;
		}
		uniqueProbes.put(probes, curTestcase);
		return true;
	}
}
