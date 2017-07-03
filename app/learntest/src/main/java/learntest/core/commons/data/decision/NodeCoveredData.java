/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import learntest.testcase.data.INodeCoveredData;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class NodeCoveredData implements INodeCoveredData {
	private List<BreakpointValue> trueValues;
	private List<BreakpointValue> falseValues;
	private List<BreakpointValue> oneTimeValues;
	private List<BreakpointValue> moreTimesValues;

	public NodeCoveredData(NodeCoverage coverage, List<BreakpointValue> newTestInputs) {
		this(coverage, newTestInputs, 0);
	}
	
	public NodeCoveredData(NodeCoverage coverage, List<BreakpointValue> newTestInputs, int newTcsFirstIdx) {
		/* collect input values of true branch */
		trueValues = getBranchCoveredValue(coverage, newTestInputs, BranchRelationship.TRUE,
				newTcsFirstIdx);
		/* collect input values of false branch */
		falseValues = getBranchCoveredValue(coverage, newTestInputs, BranchRelationship.FALSE,
				newTcsFirstIdx);
		oneTimeValues = new ArrayList<BreakpointValue>(coverage.getCoveredTcs().size());
		moreTimesValues = new ArrayList<BreakpointValue>(coverage.getCoveredTcs().size());
		updateFreqCoveredValue(coverage, newTestInputs, newTcsFirstIdx);
	}
	
	public void update(NodeCoverage coverage, int newTcsFirstIdx, List<BreakpointValue> newTestInputs) {
		trueValues.addAll(getBranchCoveredValue(coverage, newTestInputs, BranchRelationship.TRUE, newTcsFirstIdx));
		falseValues.addAll(getBranchCoveredValue(coverage, newTestInputs, BranchRelationship.FALSE, newTcsFirstIdx));
		updateFreqCoveredValue(coverage, newTestInputs, newTcsFirstIdx);
	}
	
	/**
	 * @param oneTimeValues list of one time covered input values needed to update.
	 * @param moreTimesValues list of more time covered input values needed to update.
	 * @param testOffset first test idx from which we collect the coverage
	 */
	private void updateFreqCoveredValue(NodeCoverage nodeCoverage, List<BreakpointValue> testInputs, int testOffset) {
		for (int idx : nodeCoverage.getCoveredTcs().keySet()) {
			int newTestIdx = idx - testOffset;
			if (newTestIdx < 0) {
				continue;
			}
			Integer freq = nodeCoverage.getCoveredTcs().get(idx);
			/*
			 * add to one time values if tc covers node once, otherwise, add to
			 * moretimesValues
			 */
			if (freq == 1) {
				oneTimeValues.add(testInputs.get(newTestIdx));
			} else {
				moreTimesValues.add(testInputs.get(newTestIdx));
			}
		}
	}
	
	/**
	 * 
	 * @param nodeCoverage
	 * @param testInputs 
	 * 			list of "ONLY" new test Inputs, testInput at idx (i) of the list will be the input of (testcase i - testOffset)
	 * @param branchType
	 * @param testOffset first test idx from which we collect the coverage
	 * @return
	 */
	private static List<BreakpointValue> getBranchCoveredValue(NodeCoverage nodeCoverage, List<BreakpointValue> testInputs,
			BranchRelationship branchType, int testOffset) {
		CfgNode branch = nodeCoverage.getCfgNode().getBranch(branchType);
		if (branch == null) {
			return new ArrayList<BreakpointValue>(0);
		}
		/* 
		 * if false branch is undefined
		 * ex: if (cond) {
		 * 			doA();		
		 * 		}
		 * 		doB();
		 * 
		 * in this case, if no branch of node is covered, but doB() is covered, 
		 * we still consider FALSE branch is covered.
		 * (this is the current implementation in CfgCoverage)
		 *  */
		Map<Integer, List<Integer>> coveredBranches = nodeCoverage.getCoveredBranches();
		List<Integer> tcIdxCovered = getCoveredTestIdexies(coveredBranches, branch.getIdx(), testOffset);
		int size = CollectionUtils.getSize(tcIdxCovered);
		List<BreakpointValue> values = new ArrayList<BreakpointValue>(size);
		for (Integer idx : CollectionUtils.nullToEmpty(tcIdxCovered)) {
			values.add(testInputs.get(idx));
		}
		return values;
	}

	private static List<Integer> getCoveredTestIdexies(Map<Integer, List<Integer>> coveredBranches, int branchIdx,
			int testOffset) {
		List<Integer> tcIdxCovered = coveredBranches.get(branchIdx);
		if (testOffset == 0 || CollectionUtils.isEmpty(tcIdxCovered)) {
			return tcIdxCovered;
		}
		List<Integer> newCoveredTestIdxies = new ArrayList<Integer>();
		for (int testIdx : tcIdxCovered) {
			int newCoveredTestIdx = testIdx - testOffset;
			if (newCoveredTestIdx >= 0) {
				newCoveredTestIdxies.add(newCoveredTestIdx);
			}
		}
		return newCoveredTestIdxies;
	}
	
	public boolean isOnlyOneBranchCovered() {
		return trueValues.isEmpty() || falseValues.isEmpty();
	}

	public CoveredBranches getCoveredBranches() {
		return CoveredBranches.valueOf(!trueValues.isEmpty(), !falseValues.isEmpty());
	}

	public boolean areAllbranchesUncovered() {
		return trueValues.isEmpty() && falseValues.isEmpty();
	}

	public List<BreakpointValue> getTrueValues() {
		return trueValues;
	}

	public List<BreakpointValue> getFalseValues() {
		return falseValues;
	}

	public List<BreakpointValue> getOneTimeValues() {
		return oneTimeValues;
	}

	public List<BreakpointValue> getMoreTimesValues() {
		return moreTimesValues;
	}

}
