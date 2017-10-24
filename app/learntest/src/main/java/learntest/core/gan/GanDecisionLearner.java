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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import learntest.core.commons.test.TestTools;
import learntest.core.commons.test.gan.GanTestTool;
import learntest.core.commons.utils.CfgUtils;
import learntest.core.gan.vm.GanMachine;
import learntest.core.gan.vm.NodeDataSet;
import learntest.core.gan.vm.NodeDataSet.Category;
import learntest.core.machinelearning.CfgDomain;
import learntest.core.machinelearning.CfgNodeDomainInfo;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.SampleExecutor;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecVar;
import variable.Variable;

/**
 * @author LLT
 *
 */
public class GanDecisionLearner implements IInputLearner {
	private Logger log = LoggerFactory.getLogger(GanDecisionLearner.class);
	private GanTestTool ganLog = TestTools.getInstance().gan;
	private LearningMediator mediator;
	private GanMachine machine;
	private SampleExecutor sampleExecutor;
	protected HashMap<CfgNode, CfgNodeDomainInfo> dominationMap = new HashMap<CfgNode, CfgNodeDomainInfo>();
	private Set<Integer> trainedNodes;

	public GanDecisionLearner(LearningMediator mediator) {
		this.mediator = mediator;
		machine = new GanMachine();
//		machine.setVmTimeout(600000);
		trainedNodes = new HashSet<Integer>();
	}

	@Override
	public DecisionProbes learn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap)
			throws SavException {
		this.sampleExecutor = new SampleExecutor(mediator, inputProbes);
		trainedNodes.clear();
		machine.start();
		dominationMap = new CfgDomain().constructDominationMap(CfgUtils.getVeryFirstDecisionNode(inputProbes.getCfg()));
		machine.startTrainingMethod(inputProbes.getTargetMethod().getMethodFullName());
		TrainingVariables trainingVars = new TrainingVariables() {
			private List<String> labels;
			@Override
			public List<String> getLabel(int idx) {
				if (labels == null) {
					labels = BreakpointDataUtils.extractLabels(inputProbes.getOriginalVars());
				}
				return labels;
			}

			@Override
			public List<ExecVar> getExecVars(int idx) {
				return inputProbes.getOriginalVars();
			}
			
		};
		for (CfgNode node : inputProbes.getCfg().getDecisionNodes()) {
			refineNode(node, inputProbes, trainingVars);
			
		}
		machine.stop();
		return inputProbes;
	}

	private void refineNode(CfgNode node, DecisionProbes probes, TrainingVariables trainingVars) {
		DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
		CoveredBranches coveredBranches = nodeProbe.getCoveredBranches();
		
		/* update coveredbranches */
		coveredBranches = nodeProbe.getCoveredBranches();
		if ((coveredBranches != CoveredBranches.TRUE_AND_FALSE) && (coveredBranches != CoveredBranches.NONE)) {
			expandAtNode(nodeProbe, trainingVars, getCategory(coveredBranches.getOnlyOneMissingBranch()));
			coveredBranches = nodeProbe.getCoveredBranches();
		}
		if (coveredBranches != CoveredBranches.TRUE_AND_FALSE) {
			/*
			 * generate more datapoint for its parent node for a try to get this
			 * node covered
			 */
			ambitionParentNode(nodeProbe, trainingVars);
		}
	}

	private void ambitionParentNode(DecisionNodeProbe nodeProbe, TrainingVariables trainingVars) {
		CfgNode node = nodeProbe.getNode();
		CfgNode parentNode = getParentNode(node);
		if (parentNode == null) {
			return;
		}
		expandAtNode(nodeProbe.getDecisionProbes().getNodeProbe(parentNode), trainingVars,
				getCategory(parentNode.getBranchRelationship(node.getIdx())));
	}

	private SamplingResult expandAtNode(DecisionNodeProbe nodeProbe, TrainingVariables trainingVars, Category category) {
		CfgNode node = nodeProbe.getNode();
		int nodeIdx = node.getIdx();
		train(node, nodeProbe, trainingVars);
		NodeDataSet generatedDataSet = machine.requestData(nodeIdx, trainingVars.getLabel(node.getIdx()), category);
		ganLog.log("Generated datapoints: ");
		
		SamplingResult samplingResult = null;
		if (generatedDataSet != null) {
			try {
				samplingResult = sampleExecutor.runSamples(generatedDataSet.getAllDatapoints(), trainingVars.getExecVars(node.getIdx()));
				// log new coverage
				ganLog.logFormat("new coverage: ");
				ganLog.logCoverage(nodeProbe.getDecisionProbes());
				ganLog.logAccuracy(node, samplingResult, category);
			} catch (SavException e) {
				log.debug("Error when generating new testcases: {}", e.getMessage());
			}
		}
		return samplingResult;
	}

	private void train(CfgNode node, INodeCoveredData nodeProbe, TrainingVariables trainingVars) {
		if (trainedNodes.contains(node.getIdx())) {
			return;
		}
		NodeDataSet trainingData = new NodeDataSet();
		trainingData.setLabels(trainingVars.getLabel(node.getIdx()));
		List<ExecVar> execVars = trainingVars.getExecVars(node.getIdx());
		trainingData.setDatapoints(Category.TRUE,
				BreakpointDataUtils.toDataPoint(execVars, nodeProbe.getTrueValues()));
		trainingData.setDatapoints(Category.FALSE,
				BreakpointDataUtils.toDataPoint(execVars, nodeProbe.getFalseValues()));
		ganLog.logDatapoints(node.getIdx(), trainingData);
		machine.train(node.getIdx(), trainingData);
		trainedNodes.add(node.getIdx());
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
	 * TODO-LLT: to handle for multi level, or more than 1 parent node.
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

	private static interface TrainingVariables {

		List<String> getLabel(int idx);

		List<ExecVar> getExecVars(int idx);
		
	}

	@Override
	public void cleanup() {
	}
}
