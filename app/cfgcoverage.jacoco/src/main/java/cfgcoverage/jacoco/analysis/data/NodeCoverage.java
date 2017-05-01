/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class NodeCoverage {
	private CfgCoverage cfgCoverage;
	private CfgNode cfgNode;
	private Map<String, Integer> coveredTcs;
	/* we keep node idx to refers to branch node */
	private Map<String, List<Integer>> coveredBranches;
	
	public NodeCoverage(CfgCoverage cfgCoverage, CfgNode cfgNode) {
		this.cfgCoverage = cfgCoverage;
		this.cfgNode = cfgNode;
		coveredTcs = new HashMap<String, Integer>();
		coveredBranches = new HashMap<String, List<Integer>>();
	}
	
	public void markCovered(CfgNode coveredBranch, String testMethod, int count) {
		if (coveredBranch != null) {
			updateCoveredBranchesForTc(coveredBranch, testMethod);
		}
		
		if (isCovered(testMethod)) {
			// no need to update its predecessors
			return;
		}
		// otherwise, mark covered and update all its predecessors
		setCovered(testMethod, count);
		for (CfgNode predecessor : cfgNode.getPredecessors()) {
			cfgCoverage.getCoverage(predecessor).markCovered(cfgNode, testMethod, count);
		}
	}

	public void updateCoveredBranchesForTc(CfgNode coveredBranch, String testMethod) {
		List<Integer> coveredBranchesOnTc = coveredBranches.get(testMethod);
		if (CollectionUtils.isEmpty(coveredBranchesOnTc)) {
			coveredBranchesOnTc = new ArrayList<Integer>();
			coveredBranches.put(testMethod, coveredBranchesOnTc);
		}
		coveredBranchesOnTc.add(coveredBranch.getIdx());
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
		return "NodeCoverage [" + cfgNode + ", coveredTcs=" + coveredTcs + ", coveredBranches="
				+ coveredBranches + "]";
	}
	
}
