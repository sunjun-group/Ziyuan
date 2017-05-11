/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.utils.LearningUtils;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.TestResultType;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DecisionProbes extends CfgCoverage {
	private Map<TestResultType, List<Integer>> testResults;
	private List<BreakpointValue> testInputs;
	private List<ExecVar> originalVars;
	private List<ExecVar> learningVars;
	private List<String> labels;
	
	/* TODO LLT: cache the node list, but be careful with the update */
	/* map between cfgNode idx of decision node with its probe */
	private Map<Integer, DecisionNodeProbe> nodeProbeMap;
	
	public DecisionProbes(CfgCoverage cfgCoverage) {
		super(cfgCoverage.getCfg());
		transferCoverage(cfgCoverage);
	}
	
	public void setRunningResult(List<BreakpointValue> testInputs) {
		this.testInputs = testInputs;
		originalVars = BreakpointDataUtils.collectAllVars(testInputs);
		learningVars = LearningUtils.createPolyClassifierVars(originalVars);
	}

	public void transferCoverage(CfgCoverage cfgCoverage) {
		setCfg(cfgCoverage.getCfg());
		setNodeCoverages(cfgCoverage.getNodeCoverages());
		setTestcases(cfgCoverage.getTestcases());
	}
	
	public OrCategoryCalculator getPrecondition(CfgNode node) {
		Precondition precondition = getNodeProbe(node).getPrecondition();
		for (CfgNode dominatee : CollectionUtils.nullToEmpty(node.getDominatees())) {
			Precondition domPrecond = getNodeProbe(dominatee).getPrecondition();
			List<Divider> domDividers = domPrecond.getDividers();
			if (CollectionUtils.isEmpty(domDividers)) {
				precondition.addPreconditions(domPrecond.getPreconditions());
			} else {
				/* based on branch relationship between node with its dominatee, create calculator by dividers 
				 * from the current implementation, we treat TRUE_FALSE relationship as FALSE 
				 * TODO LLT: confirm with YUN LIN.
				 * */
				BranchRelationship branchRel = node.getBranchRelationship(dominatee.getIdx());
				CategoryCalculator condFromDivicers = null;
				if (branchRel == BranchRelationship.TRUE) {
					condFromDivicers = new MultiDividerBasedCategoryCalculator(domDividers);
				} else if (dominatee.isLoopHeaderOf(node)) {
					condFromDivicers = new MultiDividerBasedCategoryCalculator(domDividers);
				}
				if (condFromDivicers != null) {
					precondition.addPreconditions(domPrecond.getPreconditions(), condFromDivicers);
				}
			}
		}
		return new OrCategoryCalculator(precondition.getPreconditions(), learningVars, originalVars);
	}

	/**
	 * in order to avoid unnecessarily generate divider for a node, we check if it is needed to learn at the current node.
	 * it is needed iff
	 * one of its dependentees is not covered
	 * */
	public boolean doesNodeNeedToLearnPrecond(DecisionNodeProbe nodeProbe) {
		for (CfgNode dependentee : CollectionUtils.nullToEmpty(nodeProbe.getNode().getDependentees())) {
			DecisionNodeProbe dependenteeProbe = getNodeProbe(dependentee);
			if (dependenteeProbe.hasUncoveredBranch()) {
				return true;
			}
		}
		return false;
	}

	public List<DecisionNodeProbe> getNodeProbes() {
		return new ArrayList<>(getNodeProbeMap().values());
	}

	/***
	 * get or build nodeProbes if not exist.
	 * @return
	 */
	public Map<Integer, DecisionNodeProbe> getNodeProbeMap() {
		if (nodeProbeMap == null) {
			nodeProbeMap = new HashMap<Integer, DecisionNodeProbe>();
			List<CfgNode> decisionNodes = getCfg().getDecisionNodes();
			for (CfgNode node : decisionNodes) {
				DecisionNodeProbe nodeProbe = new DecisionNodeProbe(getCoverage(node), testResults, testInputs);
				nodeProbeMap.put(node.getIdx(), nodeProbe);
			}
			
			/* update node dominatees */
			for (DecisionNodeProbe nodeProbe : nodeProbeMap.values()) {
				Set<CfgNode> nodeDominatees = nodeProbe.getNode().getDominatees();
				List<DecisionNodeProbe> dominatees = new ArrayList<DecisionNodeProbe>(
						CollectionUtils.getSize(nodeDominatees));
				if (nodeDominatees != null) {
					for (CfgNode node : nodeDominatees) {
						dominatees.add(getNodeProbe(node));
					}
				}
				nodeProbe.setDominatees(dominatees);
			}
		}
		return nodeProbeMap;
	}
	
	public List<BreakpointValue> getTestInputs() {
		return testInputs;
	}

	public DecisionNodeProbe getNodeProbe(CfgNode node) {
		return getNodeProbeMap().get(node.getIdx());
	}

	/**
	 * @return
	 */
	public List<String> getLabels() {
		if (labels == null) {
			labels = BreakpointDataUtils.extractLabels(learningVars);
		}
		return labels;
	}
	
	/**
	 * @return the originalVars
	 */
	public List<ExecVar> getOriginalVars() {
		return originalVars;
	}
	
}
