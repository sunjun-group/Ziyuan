/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.BranchType;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.CfgUtils;
import learntest.core.commons.utils.VariableUtils;
import learntest.core.commons.utils.VariableUtils.VarInfo;
import learntest.core.gan.vm.GanMachine;
import learntest.core.gan.vm.NodeDataSet;
import learntest.core.gan.vm.NodeDataSet.Category;
import learntest.core.machinelearning.CfgDomain;
import learntest.core.machinelearning.CfgNodeDomainInfo;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.SampleExecutor;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import variable.Variable;

/**
 * @author LLT
 *
 */
public class GanDecisionLearner implements IInputLearner {
	private LearningMediator mediator;
	private GanMachine machine;
	private SampleExecutor sampleExecutor;
	protected HashMap<CfgNode, CfgNodeDomainInfo> dominationMap = new HashMap<CfgNode, CfgNodeDomainInfo>();

	public GanDecisionLearner(LearningMediator mediator) {
		this.mediator = mediator;
		machine = new GanMachine();
	}

	@Override
	public DecisionProbes learn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap)
			throws SavException {
		this.sampleExecutor = new SampleExecutor(mediator, inputProbes);
		machine.start();
		dominationMap = new CfgDomain().constructDominationMap(CfgUtils.getVeryFirstDecisionNode(inputProbes.getCfg()));
		machine.startTrainingMethod(inputProbes.getTargetMethod().getMethodFullName());
		List<VarInfo> relevantVars = VariableUtils.varsTransform(relevantVarMap, inputProbes.getOriginalVars());
		for (CfgNode node : inputProbes.getCfg().getDecisionNodes()) {
			refineNode(node, inputProbes, relevantVars);
		}
		return inputProbes;
	}

	private void refineNode(CfgNode node, DecisionProbes probes, List<VarInfo> relevantVars) {
		DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
		VarInfo relevantVarInfo = relevantVars.get(node.getIdx());
		train(node, nodeProbe, relevantVarInfo);
		CoveredBranches coveredBranches = nodeProbe.getCoveredBranches();
		if (coveredBranches == CoveredBranches.NONE) {
			/*
			 * generate more datapoint for its parent node for a try to get this
			 * node covered
			 */
			ambitionParentNode(node, relevantVars);
		}
		if (coveredBranches != CoveredBranches.TRUE_AND_FALSE) {
			expandAtNode(node, relevantVars, getCategory(coveredBranches.getOnlyOneMissingBranch()));
		}
	}

	private void ambitionParentNode(CfgNode node, List<VarInfo> relevantVars) {
		CfgNode parentNode = getParentNode(node);
		if (parentNode == null) {
			return;
		}
		SamplingResult samplingResult = expandAtNode(parentNode, relevantVars,
				getCategory(parentNode.getBranchRelationship(node.getIdx())));
		/* train the current node with new data */
		train(node, samplingResult.getNewData(node), relevantVars.get(node.getIdx()));
		/*
		 * TODO: try to generate data from parent of parent if generated data at
		 * the very closed parent doesnot help much
		 */
	}

	private SamplingResult expandAtNode(CfgNode node, List<VarInfo> relevantVars, Category category) {
		int nodeIdx = node.getIdx();
		VarInfo varInfo = relevantVars.get(nodeIdx);
		NodeDataSet generatedDataSet = machine.requestData(nodeIdx, VariableUtils.getLabels(varInfo), category);
		SamplingResult samplingResult = null;
		if (generatedDataSet != null) {
			try {
				samplingResult = sampleExecutor.runSamples(generatedDataSet.getAllDatapoints(), varInfo.getExecVars());
			} catch (SavException e) {
				log.debug("Error when generating new testcases: {}", e.getMessage());
			}
			/* train with new data */
			train(node, samplingResult.getNewData(node), varInfo);
		}
		return samplingResult;
	}

	private void train(CfgNode node, INodeCoveredData nodeProbe, VarInfo relevantVarInfo) {
		NodeDataSet trainingData = new NodeDataSet();
		trainingData.setLabels(VariableUtils.getLabels(relevantVarInfo));
		trainingData.setDatapoints(Category.TRUE,
				BreakpointDataUtils.toDataPoint(relevantVarInfo.getExecVars(), nodeProbe.getTrueValues()));
		trainingData.setDatapoints(Category.FALSE,
				BreakpointDataUtils.toDataPoint(relevantVarInfo.getExecVars(), nodeProbe.getFalseValues()));
		machine.train(node.getIdx(), trainingData);
	}

	private Category getCategory(BranchRelationship branchRelationship) {
		switch (branchRelationship) {
		case TRUE:
			return Category.TRUE;
		case FALSE:
			return Category.FALSE;
		default:
			return null;
		}
	}

	private Category getCategory(BranchType branch) {
		switch (branch) {
		case TRUE:
			return Category.TRUE;
		case FALSE:
			return Category.FALSE;
		default:
			return null;
		}
	}

	/**
	 * TODO-LLT: to handle for multi level.
	 */
	private CfgNode getParentNode(CfgNode nodeProbe) {
		List<CfgNode> dominators = dominationMap.get(nodeProbe).getDominators(); 
		if (CollectionUtils.isEmpty(dominators)) {
			return null;
		}
		return dominators.get(0); 
	}

	@Override
	public HashMap<String, Collection<BreakpointValue>> getTrueSample() {
		return null;
	}

	@Override
	public HashMap<String, Collection<BreakpointValue>> getFalseSample() {
		return null;
	}

	@Override
	public String getLogFile() {
		return null;
	}

}
