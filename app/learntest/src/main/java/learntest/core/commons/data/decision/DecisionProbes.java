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
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.CfgUtils;
import learntest.core.commons.utils.MachineLearningUtils;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DecisionProbes extends CfgCoverage {
	private TargetMethod targetMethod;
	private List<BreakpointValue> testInputs;
	private List<ExecVar> originalVars;
	private List<ExecVar> learningVars;
	private List<String> labels;
	private int totalTestNum;
	
	/* cache the node list, but be careful with the update */
	/* map between cfgNode idx of decision node with its probe */
	private Map<Integer, DecisionNodeProbe> nodeProbeMap;
	
	public DecisionProbes(TargetMethod targetMethod, CfgCoverage cfgCoverage) {
		super(cfgCoverage.getCfg());
		this.targetMethod = targetMethod;
		transferCoverage(cfgCoverage);
		totalTestNum = cfgCoverage.getTestcases().size();
		testInputs = new CompositeList<BreakpointValue>();
	}
	
	public void setRunningResult(List<BreakpointValue> testInputs) {
		this.testInputs.addAll(testInputs);
		originalVars = BreakpointDataUtils.collectAllVars(testInputs);
		learningVars = MachineLearningUtils.createPolyClassifierVars(originalVars);
		initProbeMap(testInputs);
	}

	public void transferCoverage(CfgCoverage cfgCoverage) {
		setCfg(cfgCoverage.getCfg());
		setNodeCoverages(cfgCoverage.getNodeCoverages());
		for (NodeCoverage nodeCoverage : getNodeCoverages()) {
			nodeCoverage.setCfgCoverage(this);
		}
		this.dupTcMap = cfgCoverage.getDupTcs();
		setTestcases(cfgCoverage.getTestcases());
	}
	
	/* build precondition of a node based on its dominatees */
	public OrCategoryCalculator getPrecondition(CfgNode node) {
		Precondition precondition = getNodeProbe(node).getPrecondition();
		for (CfgNode dominator : CfgUtils.getPrecondInherentDominatee(node)) {
			Precondition domPrecond = getNodeProbe(dominator).getPrecondition();
			List<Divider> domDividers = domPrecond.getDividers();
			if (CollectionUtils.isEmpty(domDividers)) {
				precondition.addPreconditions(domPrecond.getPreconditions());
			} else {
				BranchRelationship branchRel = node.getBranchRelationship(dominator.getIdx());
				CategoryCalculator condFromDividers = null;
				if (branchRel == BranchRelationship.TRUE) {
					condFromDividers = new MultiDividerBasedCategoryCalculator(domDividers);
				} else if (dominator.isLoopHeaderOf(node)) {
					condFromDividers = new MultiDividerBasedCategoryCalculator(domDividers);
				} 
				else {
					List<Divider> clonedDividers = new ArrayList<>();
					for(Divider d: domDividers){
						double[] clonedThetas = new double[d.getThetas().length];
						for(int i=0; i<clonedThetas.length; i++){
							clonedThetas[i]=-1*d.getThetas()[i];
						}
						
						Divider d0 = new Divider(clonedThetas, -1*d.getTheta0(), true);
						clonedDividers.add(d0);
					}
					condFromDividers = new MultiDividerBasedCategoryCalculator(clonedDividers);
				}
				System.currentTimeMillis();
				if (condFromDividers != null) {
					precondition.addPreconditions(domPrecond.getPreconditions(), condFromDividers);
				}
			}
		}
		System.currentTimeMillis();
		if (!precondition.getPreconditions().isEmpty() && precondition.getPreconditions().get(0).size()>20) {
			System.currentTimeMillis();
		}
		return new OrCategoryCalculator(precondition.getPreconditions(), learningVars, originalVars);
	}

	/**
	 * in order to avoid unnecessarily generate divider for a node, we check if it is needed to learn at the current node.
	 * it is needed iff
	 * one of its dependentees is not covered
	 **/
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
		return new ArrayList<DecisionNodeProbe>(getNodeProbeMap().values());
	}

	public Map<Integer, DecisionNodeProbe> getNodeProbeMap() {
		return nodeProbeMap;
	}

	private void initProbeMap(List<BreakpointValue> initTestInputs) {
		if (CollectionUtils.isEmpty(nodeProbeMap)) {
			nodeProbeMap = new HashMap<Integer, DecisionNodeProbe>();
			List<CfgNode> decisionNodes = getCfg().getDecisionNodes();
			for (CfgNode node : decisionNodes) {
				DecisionNodeProbe nodeProbe = new DecisionNodeProbe(this, getCoverage(node), initTestInputs);
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
	
	public boolean isOutOfDate() {
		return this.totalTestNum != super.getTestcases().size();
	}
	
	/**
	 * whenever the probes object is updated, this method should be call to make sure 
	 * the getting data is not out of date.
	 * @param newTcsFirstIdx 
	 * @param newTestInputs 
	 */
	public void update(int newTcsFirstIdx, List<BreakpointValue> newTestInputs) {
		if (nodeProbeMap != null && isOutOfDate()) {
			for (DecisionNodeProbe nodeProbe : nodeProbeMap.values()) {
				nodeProbe.update(newTcsFirstIdx, newTestInputs);
			}
			this.testInputs.addAll(newTestInputs);
			totalTestNum = getTestcases().size();
		}
	}
	
	public int getTotalTestNum() {
		return totalTestNum;
	}
	
	public TargetMethod getTargetMethod() {
		return targetMethod;
	}
}
