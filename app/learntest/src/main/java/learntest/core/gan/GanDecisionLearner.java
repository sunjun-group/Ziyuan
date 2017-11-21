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
import learntest.core.machinelearning.AbstractDecisionLearner;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.SampleExecutor;
import sav.common.core.SavException;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.execute.value.ExecVar;
import variable.Variable;

/**
 * @author LLT
 */
public class GanDecisionLearner extends AbstractDecisionLearner implements IInputLearner {
	private Logger log = LoggerFactory.getLogger(GanDecisionLearner.class);
	private GanTestTool ganLog = TestTools.getInstance().gan;
	
	private GanMachine machine;
	private SampleExecutor sampleExecutor;
	private TrainingVariables trainingVars;
	private Set<Integer> ganTrainedNodes;
	
	public GanDecisionLearner(LearningMediator mediator) {
		super(mediator);
		machine = new GanMachine();
		machine.setVmTimeout(5000000);
	}
	
	@Override
	protected void prepareDataBeforeLearn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap) throws SavException {
		this.sampleExecutor = new SampleExecutor(mediator, inputProbes);
		ganTrainedNodes = new HashSet<Integer>();
		machine.start();
		machine.startTrainingMethod(inputProbes.getTargetMethod().getMethodFullName());
		trainingVars = new TrainingVariables() {
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
	}
	
	@Override
	protected void onFinishLearning() {
		machine.stop();
	}

	@Override
	protected CfgNode learn(DecisionNodeProbe nodeProbe, List<Integer> visitedNodes, int loopTimes) throws SavException {
		CfgNode node = nodeProbe.getNode();
		/* TODO LLT: not sure about loop times here, 
		 * there is a case in which one of dominators of loopheader is itself,
		 * so we need to prevent the infinitive loop here
		 */
		if (needToLearn(nodeProbe) && loopTimes < 10) {
			List<CfgNode> dominators = getDominators(node);
			for (CfgNode dominator : dominators) {
				if (!visitedNodes.contains(dominator.getIdx())) {
					return node;
				}
			}
			/* valid to be learned */
			train(node, nodeProbe, trainingVars);
			// sampling
			BranchRelationship type = getSamplingBranches(nodeProbe);
			if (CfgUtils.implyTrueBranch(type)) {
				sampling(nodeProbe, trainingVars, Category.TRUE);
			}
			if (CfgUtils.implyFalseBranch(type)) {
				sampling(nodeProbe, trainingVars, Category.FALSE);
			}
		} 
		return null;
	}

	private BranchRelationship getSamplingBranches(DecisionNodeProbe nodeProbe) {
		BranchRelationship type = null;
		CoveredBranches coveredBranches = nodeProbe.getCoveredBranches();
		BranchType missingBranch = coveredBranches.getOnlyOneMissingBranch();
		if (missingBranch != null) {
			type = missingBranch.toBranchRelationship();
		}
		/* check its dependentees */
		DecisionProbes probes = nodeProbe.getDecisionProbes();
		for (CfgNode dependentee : dominationMap.get(nodeProbe.getNode()).getDominatees()) {
			DecisionNodeProbe dependenteeProbe = probes.getNodeProbe(dependentee);
			if (dependenteeProbe.hasUncoveredBranch()) {
				type = BranchRelationship.merge(type, nodeProbe.getNode().getBranchRelationship(dependentee.getIdx()));
			}
		}
		return type;
	}
	
	private SamplingResult sampling(DecisionNodeProbe nodeProbe, TrainingVariables trainingVars, Category category) {
		CfgNode node = nodeProbe.getNode();
		int nodeIdx = node.getIdx();
		NodeDataSet generatedDataSet = machine.requestData(nodeIdx, trainingVars.getLabel(node.getIdx()), category);
		ganLog.log("Generated datapoints: ");
		SamplingResult samplingResult = null;
		if (generatedDataSet == null) {
			ganLog.log("empty generatedDataSet!");
		} else {
			ganLog.logFormat("NodeIdx={}", generatedDataSet.getNodeId());
			for (Category cat : Category.values()) {
				ganLog.logFormat("{}: ", cat.name());
				ganLog.log(TextFormatUtils.printCol(generatedDataSet.getDataset().get(cat), "\n"));
			}
			try {
				List<double[]> allDatapoints = generatedDataSet.getAllDatapoints();
				samplingResult = sampleExecutor.runSamples(allDatapoints, trainingVars.getExecVars(node.getIdx()));
				// log new coverage
				ganLog.logFormat("new coverage: ");
				ganLog.logCoverage(nodeProbe.getDecisionProbes());
				ganLog.logSamplingResult(node, allDatapoints, samplingResult, category);
			} catch (SavException e) {
				log.debug("Error when generating new testcases: {}", e.getMessage());
			}
		}
		return samplingResult;
	}

	private void train(CfgNode node, INodeCoveredData nodeProbe, TrainingVariables trainingVars) {
		if (ganTrainedNodes.contains(node.getIdx())) {
			return;
		}
		NodeDataSet trainingData = new NodeDataSet();
		trainingData.setLabels(trainingVars.getLabel(node.getIdx()));
		List<ExecVar> execVars = trainingVars.getExecVars(node.getIdx());
		trainingData.setDatapoints(Category.TRUE,
				BreakpointDataUtils.toDataPoint(execVars, nodeProbe.getTrueValues()));
		trainingData.setDatapoints(Category.FALSE,
				BreakpointDataUtils.toDataPoint(execVars, nodeProbe.getFalseValues()));
		trainingData.setNodeId(String.valueOf(node.getIdx()));
		ganLog.logTrainDatapoints(node, trainingData);
		machine.train(node.getIdx(), trainingData);
		ganTrainedNodes.add(node.getIdx());
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
