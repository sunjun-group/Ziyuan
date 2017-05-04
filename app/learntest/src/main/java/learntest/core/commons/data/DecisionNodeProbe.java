/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import learntest.testcase.data.BranchType;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.TestResultType;

/**
 * @author LLT
 *
 */
public class DecisionNodeProbe implements IDecisionNode {
	private CfgNode cfgNode;
	
	/* LLT: TO REFACTOR? */
	private List<BreakpointValue> trueValues;
	private List<BreakpointValue> falseValues;
	private List<BreakpointValue> oneTimeValues;
	private List<BreakpointValue> moreTimesValues;

	public DecisionNodeProbe(NodeCoverage nodeCoverage, Map<TestResultType, List<Integer>> testResults,
			List<BreakpointValue> testInputs) {
		cfgNode = nodeCoverage.getCfgNode();
		Map<Integer, List<Integer>> coveredBranches = nodeCoverage.getCoveredBranches();
		/* true branch is first branch of cfg node */
		trueValues = getCoveredInputValues(coveredBranches.get(0), testInputs);
		/* false branch is the second branch of cfg node */
		falseValues = getCoveredInputValues(coveredBranches.get(1), testInputs);
		oneTimeValues = new ArrayList<BreakpointValue>(nodeCoverage.getCoveredTcs().size());
		moreTimesValues = new ArrayList<BreakpointValue>(nodeCoverage.getCoveredTcs().size());
		for (int idx : nodeCoverage.getCoveredTcs().keySet()) {
			Integer freq = nodeCoverage.getCoveredTcs().get(idx);
			/* add to one time values if tc covers node once, otherwise, add to moretimesValues */
			if (freq == 1) {
				oneTimeValues.add(testInputs.get(idx));
			} else {
				moreTimesValues.add(testInputs.get(idx));
			}
		}
	}

	private List<BreakpointValue> getCoveredInputValues(List<Integer> tcIdxCovered, List<BreakpointValue> testInputs) {
		int size = CollectionUtils.getSize(tcIdxCovered);
		List<BreakpointValue> values = new ArrayList<BreakpointValue>(size);
		for (Integer idx : tcIdxCovered) {
			values.add(testInputs.get(idx));
		}
		return values;
	}

	public List<BreakpointValue> getTrueValues() {
		return trueValues;
	}

	public void setTrueValues(List<BreakpointValue> trueValues) {
		this.trueValues = trueValues;
	}

	public List<BreakpointValue> getFalseValues() {
		return falseValues;
	}

	public void setFalseValues(List<BreakpointValue> falseValues) {
		this.falseValues = falseValues;
	}

	public List<BreakpointValue> getOneTimeValues() {
		return oneTimeValues;
	}

	public void setOneTimeValues(List<BreakpointValue> oneTimeValues) {
		this.oneTimeValues = oneTimeValues;
	}

	public List<BreakpointValue> getMoreTimesValues() {
		return moreTimesValues;
	}

	public void setMoreTimesValues(List<BreakpointValue> moreTimesValues) {
		this.moreTimesValues = moreTimesValues;
	}

	public CfgNode getNode() {
		return cfgNode;
	}

	/**
	 * @return
	 */
	public boolean isOnlyOneBranchCovered() {
		return trueValues.isEmpty() || falseValues.isEmpty();
	}

	public BranchType getMissingBranch() {
		if (trueValues.isEmpty()) {
			return BranchType.TRUE;
		} 
		if (falseValues.isEmpty()) {
			return BranchType.FALSE;
		}
		return null;
	}

}
