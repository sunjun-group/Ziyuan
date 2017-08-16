/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.HashMap;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.machinelearning.CfgNodeDomainInfo;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import libsvm.core.Divider;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class DecisionNodeProbe implements IDecisionNode {
	/* reference to cfg node */
	private NodeCoverage coverage;
	/* reference to its parent */
	private DecisionProbes decisionProbes;
	
	/* node precondition */
	private Precondition precondition = new Precondition();
	private List<DecisionNodeProbe> dominatees;
	
	private CompositeNodeCoveredData coveredData;

	public DecisionNodeProbe(DecisionProbes probes, NodeCoverage nodeCoverage,
			List<BreakpointValue> testInputs) {
		this.decisionProbes = probes;
		this.coverage = nodeCoverage;
		coveredData = new CompositeNodeCoveredData(nodeCoverage, testInputs);
	}
	
	public void update(int newTcsFirstIdx, List<BreakpointValue> newTestInputs) {
		coveredData.update(coverage, newTcsFirstIdx, newTestInputs);
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
	public OrCategoryCalculator getPreconditions(HashMap<CfgNode, CfgNodeDomainInfo> dominationMap) {
		if (cachePreconditions == null) {
			cachePreconditions = decisionProbes.getPrecondition(getNode(), dominationMap);
		}
		return cachePreconditions;
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
		List<BreakpointValue> trueV = getTrueValues(), falseV = coveredData.getFalseValues();
		for (BreakpointValue bpv : trueV) {
			falseV.remove(bpv);
		}
		return falseV;
	}

	public List<BreakpointValue> getOneTimeValues() {
		return coveredData.getOneTimeValues();
	}

	public List<BreakpointValue> getMoreTimesValues() {
		return coveredData.getMoreTimesValues();
	}

	public void clearCache() {
		coveredData.clearCache();
	}

}
