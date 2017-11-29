/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;

/**
 * @author LLT
 *
 */
public class NodeCoveredData extends AbstractNodeCoveredData implements INodeCoveredData {
	private List<BreakpointValue> newTestInputs;

	public NodeCoveredData(NodeCoverage coverage, List<BreakpointValue> newTestInputs) {
		this(coverage, newTestInputs, 0);
	}
	
	private NodeCoveredData(NodeCoverage coverage, List<BreakpointValue> newTestInputs, int newTcsFirstIdx) {
		this.newTestInputs = newTestInputs;
		/* collect input values of true branch */
		trueValues = getBranchCoveredValue(coverage, newTestInputs, DecisionBranchType.TRUE,
				newTcsFirstIdx);
		/* collect input values of false branch */
		falseValues = getBranchCoveredValue(coverage, newTestInputs, DecisionBranchType.FALSE,
				newTcsFirstIdx);
		oneTimeValues = getFreqCoveredValue(coverage, newTestInputs, newTcsFirstIdx, false);
		moreTimesValues = getFreqCoveredValue(coverage, newTestInputs, newTcsFirstIdx, true);
	}
	
	public void update(NodeCoverage coverage, int newTcsFirstIdx, List<BreakpointValue> newTestInputs) {
		trueValues.addAll(getBranchCoveredValue(coverage, newTestInputs, DecisionBranchType.TRUE, newTcsFirstIdx));
		falseValues.addAll(getBranchCoveredValue(coverage, newTestInputs, DecisionBranchType.FALSE, newTcsFirstIdx));
		oneTimeValues.addAll(getFreqCoveredValue(coverage, newTestInputs, newTcsFirstIdx, false));
		moreTimesValues.addAll(getFreqCoveredValue(coverage, newTestInputs, newTcsFirstIdx, true));
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
		for (BreakpointValue bkp : getTrueValues()) {
			falseValues.remove(bkp);
		}
		return falseValues;
	}

	public List<BreakpointValue> getOneTimeValues() {
		return oneTimeValues;
	}

	public List<BreakpointValue> getMoreTimesValues() {
		return moreTimesValues;
	}

	@Override
	public List<BreakpointValue> getAllInputValues() {
		return newTestInputs;
	}
}
