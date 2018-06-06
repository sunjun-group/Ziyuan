/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cfg.CfgNode;
import cfg.DecisionBranchType;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public abstract class AbstractNodeCoveredData implements INodeCoveredData {
	protected List<BreakpointValue> trueValues;
	protected List<BreakpointValue> falseValues;
	protected List<BreakpointValue> oneTimeValues;
	protected List<BreakpointValue> moreTimesValues;

	/**
	 * @param oneTimeValues
	 *            list of one time covered input values needed to update.
	 * @param moreTimesValues
	 *            list of more time covered input values needed to update.
	 * @param testOffset
	 *            first test idx from which we collect the coverage
	 * @return
	 */
	protected static List<BreakpointValue> getFreqCoveredValue(NodeCoverage nodeCoverage,
			List<BreakpointValue> testInputs, int testOffset, boolean moretime) {
		List<BreakpointValue> values = new ArrayList<BreakpointValue>(nodeCoverage.getCoveredTcsTotal());
		int lastIdx = testInputs.size() - 1;
		for (Iterator<int[]> it = nodeCoverage.coveredTcsIterator(); it.hasNext();) {
			int[] coveredInfo = it.next();
			int newTestIdx = coveredInfo[0] - testOffset;
			if (newTestIdx < 0 || newTestIdx > lastIdx) {
				continue;
			}
			/*
			 * add to one time values if tc covers node once, otherwise, add to
			 * moretimesValues
			 */
			if ((coveredInfo[1] == 1 && !moretime) || (coveredInfo[1] > 1 && moretime)) {
				values.add(testInputs.get(newTestIdx));
			}
		}
		return values;
	}

	/**
	 * 
	 * @param nodeCoverage
	 * @param testInputs
	 *            list of "ONLY" new test Inputs, testInput at idx (i) of the
	 *            list will be the input of (testcase i - testOffset)
	 * @param branchType
	 * @param testOffset
	 *            first test idx from which we collect the coverage
	 * @return
	 */
	protected static List<BreakpointValue> getBranchCoveredValue(NodeCoverage nodeCoverage,
			List<BreakpointValue> testInputs, DecisionBranchType branchType, int testOffset) {
		CfgNode branch = nodeCoverage.getCfgNode().getDecisionBranch(branchType);
		if (branch == null) {
			return new ArrayList<BreakpointValue>(0);
		}
		/*
		 * if false branch is undefined ex: if (cond) { doA(); } doB();
		 * 
		 * in this case, if no branch of node is covered, but doB() is covered,
		 * we still consider FALSE branch is covered. (this is the current
		 * implementation in CfgCoverage)
		 */
		List<Integer> coveredTcsForBranch = nodeCoverage.getCoveredTcsForBranch(branch.getIdx());
		int lastIdx = testInputs.size() - 1;
		List<Integer> tcIdxCovered = getCoveredTestIdexies(coveredTcsForBranch, testOffset, lastIdx);
		int size = CollectionUtils.getSize(tcIdxCovered);
		List<BreakpointValue> values = new ArrayList<BreakpointValue>(size);
		for (Integer idx : CollectionUtils.nullToEmpty(tcIdxCovered)) {
			values.add(testInputs.get(idx));
		}
		return values;
	}

	private static List<Integer> getCoveredTestIdexies(List<Integer> tcIdxCovered, int firstIdx, int lastIdx) {
		if (CollectionUtils.isEmpty(tcIdxCovered)) {
			return tcIdxCovered;
		}
		List<Integer> newCoveredTestIdxies = new ArrayList<Integer>(lastIdx + 1);
		for (int testIdx : tcIdxCovered) {
			int newCoveredTestIdx = testIdx - firstIdx;
			if (newCoveredTestIdx >= 0 && newCoveredTestIdx <= lastIdx) {
				newCoveredTestIdxies.add(newCoveredTestIdx);
			}
		}
		return newCoveredTestIdxies;
	}

}
