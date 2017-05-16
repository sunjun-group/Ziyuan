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

import sav.common.core.utils.Assert;
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
	/* we keep node idx to refers to branch node 
	 * in case a false branch is missing, we use next node idx to map with coverage of missing branch.
	 * */
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

	/**
	 * update the map that keeps which branch is covered by which testcases
	 * @param coveredBranch
	 * @param testIdx
	 */
	public void updateCoveredBranchesForTc(CfgNode coveredBranch, int testIdx) {
		int branchId = coveredBranch.getIdx();
		List<Integer> coverTcs = coveredBranches.get(branchId);
		if (CollectionUtils.isEmpty(coverTcs)) {
			coverTcs = new ArrayList<Integer>();
			coveredBranches.put(branchId, coverTcs);
		}
		coverTcs.add(testIdx);
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

	/**
	 * @return map is always not null
	 */
	public Map<Integer, Integer> getCoveredTcs() {
		return coveredTcs;
	}

	/**
	 * @return map is always not null
	 */
	public Map<Integer, List<Integer>> getCoveredBranches() {
		return coveredBranches;
	}

	@Override
	public String toString() {
		return "NodeCoverage [" + cfgNode + ", coveredTcs=" + coveredTcs + ", coveredBranches="
				+ coveredBranches + "]";
	}

	/**
	 * in case of a false branch of a decision node is undefined,
	 * branch coverage for it will not be update in nodeCoverage (as default behavior of jacoco),
	 * so we have to do it manually for that case, 
	 * if next node after condition block is covered and true branch is not covered, then false branch is covered,
	 * otherwise if true branch is covered then false branch is of course uncovered.
	 */
	public void solveMissingBranchesCoverage() {
		if (!cfgNode.isDecisionNode() || coveredTcs.isEmpty()) {
			return;
		}
		CfgNode trueBranch = cfgNode.findBranch(BranchRelationship.TRUE);
		Assert.assertNotNull(trueBranch, "missing True branch: " + cfgNode.getIdx());
		CfgNode falseBranch = cfgNode.getBranch(BranchRelationship.FALSE);
		if (falseBranch == null) {
			return;
		}
		/*
		 * using next node to get coverage of undefined branch.
		 */
		Map<Integer, Integer> coveredTcsOfNextNode = cfgCoverage.getNodeCoverages().get(falseBranch.getIdx())
				.getCoveredTcs();
		List<Integer> trueCoveredTcs = coveredBranches.get(trueBranch.getIdx());
		
		List<Integer> falseCoveredTcs = coveredBranches.get(falseBranch.getIdx());
		if (falseCoveredTcs == null) {
			falseCoveredTcs = new ArrayList<Integer>();
			coveredBranches.put(falseBranch.getIdx(), falseCoveredTcs);
		}
		
		for (Integer testIdx : coveredTcs.keySet()) {
			if ((trueCoveredTcs == null || !trueCoveredTcs.contains(testIdx))
					&& coveredTcsOfNextNode.containsKey(testIdx)) {
				falseCoveredTcs.add(testIdx);
			}
		}
	}

}
