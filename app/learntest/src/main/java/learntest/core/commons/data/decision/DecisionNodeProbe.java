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

import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.utils.CfgUtils;
import learntest.testcase.data.IBreakpointData;
import libsvm.core.Divider;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.TestResultType;

/**
 * @author LLT
 *
 */
public class DecisionNodeProbe implements IDecisionNode, IBreakpointData {
	/* reference to cfg node */
	private NodeCoverage coverage;
	/* reference to its parent */
	private DecisionProbes decisionProbes;
	
	/* node precondition */
	private Precondition precondition = new Precondition();
	private List<DecisionNodeProbe> dominatees;
	
	private List<BreakpointValue> trueValues;
	private List<BreakpointValue> falseValues;
	private List<BreakpointValue> oneTimeValues;
	private List<BreakpointValue> moreTimesValues;

	public DecisionNodeProbe(NodeCoverage nodeCoverage, Map<TestResultType, List<Integer>> testResults,
			List<BreakpointValue> testInputs) {
		this.coverage = nodeCoverage;
		Map<Integer, List<Integer>> coveredBranches = nodeCoverage.getCoveredBranches();
		/* true branch is first branch of cfg node */
		CfgNode trueBranch = CfgUtils.getTrueBranch(nodeCoverage.getCfgNode());
		trueValues = getCoveredInputValues(coveredBranches, trueBranch, testInputs);
		/* false branch is the second branch of cfg node */
		CfgNode falseBranch = CfgUtils.getFalseBranch(nodeCoverage.getCfgNode());
		falseValues = getCoveredInputValues(coveredBranches, falseBranch, testInputs);
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

	private List<BreakpointValue> getCoveredInputValues(Map<Integer, List<Integer>> coveredBranches, CfgNode branch,
			List<BreakpointValue> testInputs) {
		if (branch == null) {
			return new ArrayList<BreakpointValue>(0);
		}
		List<Integer> tcIdxCovered = coveredBranches.get(branch.getIdx());
		int size = CollectionUtils.getSize(tcIdxCovered);
		List<BreakpointValue> values = new ArrayList<BreakpointValue>(size);
		for (Integer idx : CollectionUtils.nullToEmpty(tcIdxCovered)) {
			values.add(testInputs.get(idx));
		}
		return values;
	}

	public boolean isOnlyOneBranchCovered() {
		return trueValues.isEmpty() || falseValues.isEmpty();
	}

	public CoveredBranches getCoveredBranches() {
		return CoveredBranches.valueOf(!trueValues.isEmpty(), !falseValues.isEmpty());
	}

	public boolean areAllbranchesMissing() {
		return trueValues.isEmpty() && falseValues.isEmpty();
	}

	/**
	 * @param classifier
	 */
	public void setPrecondition(Pair<Formula, Formula> classifier, List<Divider> dividers) {
		setNodePrecondition(classifier, dividers);
	}

	private void setNodePrecondition(Pair<Formula, Formula> classifier, List<Divider> dividers) {
		precondition.setTrueFalse(classifier.first());
		if (getNode().isInLoop()) {
			precondition.setOneMore(classifier.second());
		}
		precondition.setDividers(dividers);
	}
	
	public Precondition getPrecondition() {
		return precondition;
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
	
	public List<DecisionNodeProbe> getDominatees() {
		return dominatees;
	}

	public void setDominatees(List<DecisionNodeProbe> dominatees) {
		this.dominatees = dominatees;
	}

	public CfgNode getNode() {
		return coverage.getCfgNode();
	}
	
	public NodeCoverage getCoverage() {
		return coverage;
	}

	/**
	 * @return
	 */
	public boolean hasUncoveredBranch() {
		int branchSize = CollectionUtils.getSize(getNode().getBranches());
		return branchSize > coverage.getCoveredBranches().size();
	}

	/**
	 * @return
	 */
	public boolean needToLearnPrecond() {
		return decisionProbes.doesNodeNeedToLearnPrecond(this);
	}
	
	/**
	 * @return the decisionProbes
	 */
	public DecisionProbes getDecisionProbes() {
		return decisionProbes;
	}

	private OrCategoryCalculator cachePreconditions;
	public OrCategoryCalculator getPreconditions() {
		if (cachePreconditions == null) {
			cachePreconditions = decisionProbes.getPrecondition(getNode());
		}
		return cachePreconditions;
	}
}
