/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import learntest.calculator.OrCategoryCalculator;
import learntest.testcase.data.INodeCoveredData;
import libsvm.core.Divider;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.TestResultType;

/**
 * @author LLT
 *
 */
public class DecisionNodeProbe implements IDecisionNode, INodeCoveredData {
	/* reference to cfg node */
	private NodeCoverage coverage;
	/* reference to its parent */
	private DecisionProbes decisionProbes;
	
	/* node precondition */
	private Precondition precondition = new Precondition();
	private List<DecisionNodeProbe> dominatees;
	
	private NodeCoveredData coveredData;

	public DecisionNodeProbe(DecisionProbes probes, NodeCoverage nodeCoverage, Map<TestResultType, List<Integer>> testResults,
			List<BreakpointValue> testInputs) {
		this.decisionProbes = probes;
		this.coverage = nodeCoverage;
		coveredData = new NodeCoveredData(nodeCoverage, testInputs);
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

	public boolean isOnlyOneBranchCovered() {
		return coveredData.isOnlyOneBranchCovered();
	}

	public CoveredBranches getCoveredBranches() {
		return coveredData.getCoveredBranches();
	}

	public boolean areAllbranchesUncovered() {
		return coveredData.areAllbranchesUncovered();
	}

	public List<BreakpointValue> getTrueValues() {
		return coveredData.getTrueValues();
	}

	public List<BreakpointValue> getFalseValues() {
		return coveredData.getFalseValues();
	}

	public List<BreakpointValue> getOneTimeValues() {
		return coveredData.getOneTimeValues();
	}

	public List<BreakpointValue> getMoreTimesValues() {
		return coveredData.getMoreTimesValues();
	}
	
}
