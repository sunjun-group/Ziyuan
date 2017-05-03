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
	/* map between testcase idx and covered frequency */
	private Map<Integer, Integer> coveredTcs;
	/* we keep node idx to refers to branch node */
	private Map<Integer, List<Integer>> coveredBranches;
	
	public NodeCoverage(CfgCoverage cfgCoverage, CfgNode cfgNode) {
		this.cfgCoverage = cfgCoverage;
		this.cfgNode = cfgNode;
		coveredTcs = new HashMap<Integer, Integer>();
		coveredBranches = new HashMap<Integer, List<Integer>>();
	}
	
	public void markCovered(CfgNode coveredBranch, int testIndx, int count) {
		if (coveredBranch != null) {
			updateCoveredBranchesForTc(coveredBranch, testIndx);
		}
		
		if (isCovered(testIndx)) {
			// no need to update its predecessors
			return;
		}
		// otherwise, mark covered and update all its predecessors
		setCovered(testIndx, count);
		for (CfgNode predecessor : cfgNode.getPredecessors()) {
			cfgCoverage.getCoverage(predecessor).markCovered(cfgNode, testIndx, count);
		}
	}

	public void updateCoveredBranchesForTc(CfgNode coveredBranch, int testIndx) {
		List<Integer> coveredBranchesOnTc = coveredBranches.get(testIndx);
		if (CollectionUtils.isEmpty(coveredBranchesOnTc)) {
			coveredBranchesOnTc = new ArrayList<Integer>();
			coveredBranches.put(testIndx, coveredBranchesOnTc);
		}
		coveredBranchesOnTc.add(coveredBranch.getIdx());
	}
	
	public void setCovered(int testIndx, int count) {
		Integer coveredCount = coveredTcs.get(testIndx);
		coveredCount = coveredCount == null ? count : coveredCount + count;
		coveredTcs.put(testIndx, coveredCount);
	}

	public boolean isCovered(int testIndx) {
		Integer coveredCount = coveredTcs.get(testIndx);
		if (coveredCount != null && coveredCount > 0) {
			return true;
		}
		return false;
	}
	
	public CfgCoverage getCfgCoverage() {
		return cfgCoverage;
	}

	public CfgNode getCfgNode() {
		return cfgNode;
	}

	public Map<Integer, Integer> getCoveredTcs() {
		return coveredTcs;
	}

	public Map<Integer, List<Integer>> getCoveredBranches() {
		return coveredBranches;
	}

	@Override
	public String toString() {
		return "NodeCoverage [" + cfgNode + ", coveredTcs=" + coveredTcs + ", coveredBranches="
				+ coveredBranches + "]";
	}
	
}
