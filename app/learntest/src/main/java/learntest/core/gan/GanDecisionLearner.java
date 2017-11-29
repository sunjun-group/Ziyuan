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
import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.BranchType;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.TestTools;
import learntest.core.commons.test.gan.GanTestTool;
import learntest.core.gan.vm.BranchDataSet;
import learntest.core.gan.vm.BranchDataSet.Category;
import learntest.core.gan.vm.GanMachine;
import learntest.core.machinelearning.AbstractDecisionLearner;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.SampleExecutor;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
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
	private InputDatapointMapping inputDpMapping;
	
	public GanDecisionLearner(LearningMediator mediator) {
		super(mediator);
		machine = new GanMachine();
		machine.setVmTimeout(3600000);
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
		inputDpMapping = new InputDatapointMapping(inputProbes.getOriginalVars());
	}
	
	@Override
	protected void onFinishLearning() {
		machine.stop();
		inputDpMapping.clear();
	}

	@Override
	protected CfgNode learn(DecisionNodeProbe nodeProbe, List<Integer> visitedNodes, int loopTimes) throws SavException {
		CfgNode node = nodeProbe.getNode();
		if (needToLearn(nodeProbe)) {
			List<CfgNode> dominators = getDominators(node);
			for (CfgNode dominator : dominators) {
				/* 
				 * TODO LLT: this is workaround for infinitive loop in the case of loop header.
				 */
				if (!visitedNodes.contains(dominator.getIdx()) && (!node.isLoopHeaderOf(dominator)
						&& (!node.isLoopHeader() || (node.getIdx() > dominator.getIdx())))) {
					return node;
				}
			}
			/* valid to be learned */
			train(node, nodeProbe, trainingVars);
			// sampling
			BranchRelationship type = getSamplingBranches(nodeProbe);
//			if (CfgUtils.implyTrueBranch(type)) {
				sampling(nodeProbe, trainingVars, DecisionBranchType.TRUE);
//			}
//			if (CfgUtils.implyFalseBranch(type)) {
				sampling(nodeProbe, trainingVars, DecisionBranchType.FALSE);
//			}
		} 
		return null;
	}

	private BranchRelationship getSamplingBranches(DecisionNodeProbe nodeProbe) {
		BranchRelationship type = null;
		System.out.println();
		CoveredBranches coveredBranches = nodeProbe.getCoveredBranches();
		BranchType missingBranch = coveredBranches.getOnlyOneMissingBranch();
		if (missingBranch != null) {
			type = missingBranch.toBranchRelationship();
		}
		/* check its dependentees */
		DecisionProbes probes = nodeProbe.getDecisionProbes();
		CfgNode node = nodeProbe.getNode();
		for (CfgNode dependentee : dominationMap.get(node).getDominatees()) {
			DecisionNodeProbe dependenteeProbe = probes.getNodeProbe(dependentee);
			if (dependentee.isLoopHeader() && (dependentee.getIdx() > node.getIdx())) {
				continue;
			}
			if (dependenteeProbe.hasUncoveredBranch()) {
				type = BranchRelationship.merge(type, node.getBranchRelationship(dependentee.getIdx()));
			}
		}
		return type;
	}
	
	private SamplingResult sampling(DecisionNodeProbe nodeProbe, TrainingVariables trainingVars, DecisionBranchType branchType) {
		CfgNode node = nodeProbe.getNode();
		int nodeIdx = node.getIdx();
		BranchDataSet generatedDataSet = machine.requestData(nodeIdx, trainingVars.getLabel(node.getIdx()), branchType);
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
				ganLog.logSamplingResult(node, allDatapoints, samplingResult, branchType);
			} catch (SavException e) {
				log.debug("Error when generating new testcases: {}", e.getMessage());
			}
		}
		return samplingResult;
	}

	private void train(CfgNode node, DecisionNodeProbe nodeProbe, TrainingVariables trainingVars) {
		if (ganTrainedNodes.contains(node.getIdx())) {
			return;
		}
		trainBranch(nodeProbe, DecisionBranchType.TRUE, nodeProbe.getTrueValues());
		trainBranch(nodeProbe, DecisionBranchType.FALSE, nodeProbe.getFalseValues());
		ganTrainedNodes.add(node.getIdx());
	}
	
	private void trainBranch(DecisionNodeProbe nodeProbe, DecisionBranchType branchType, List<BreakpointValue> coveredInputs) {
		CfgNode node = nodeProbe.getNode();
		BranchDataSet trainingData = new BranchDataSet();
		trainingData.setNodeId(String.valueOf(node.getIdx()));
		trainingData.setBranchType(branchType);
		trainingData.setLabels(trainingVars.getLabel(node.getIdx()));
		List<ExecVar> execVars = trainingVars.getExecVars(node.getIdx());
		trainingData.setDatapoints(Category.TRUE, inputDpMapping.getDatapoints(coveredInputs, execVars));
		trainingData.setDatapoints(Category.FALSE, inputDpMapping
				.getDatapoints(CollectionUtils.subtract(nodeProbe.getAllInputValues(), coveredInputs), execVars));
		ganLog.logTrainDatapoints(node, branchType, trainingData);
		machine.train(node.getIdx(), branchType, trainingData);
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
