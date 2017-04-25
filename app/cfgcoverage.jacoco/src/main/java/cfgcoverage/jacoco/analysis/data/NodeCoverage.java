/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LLT
 *
 */
public class NodeCoverage {
	private Map<String, Integer> coveredTcs;
	
	public NodeCoverage() {
		coveredTcs = new HashMap<String, Integer>();
	}

	public void setCovered(String testMethod, int count) {
		Integer coveredCount = coveredTcs.get(testMethod);
		coveredCount = coveredCount == null ? count : coveredCount + count;
		coveredTcs.put(testMethod, coveredCount);
	}

	public boolean isCovered(String testMethod) {
		Integer coveredCount = coveredTcs.get(testMethod);
		if (coveredCount != null && coveredCount > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return coveredTcs.toString();
	}
}
