/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DecisionLearner {
	protected static Logger log = LoggerFactory.getLogger(DecisionLearner.class);
	private LearnedDataProcessor dataPreprocessor;

	public void learn(DecisionProbes inputProbes) throws SavException {
		List<CfgNode> decisionNodes = inputProbes.getCfg().getDecisionNodes();
		DecisionProbes probes = inputProbes;

		for (CfgNode node : decisionNodes) {
			DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
			if (nodeProbe.areAllbranchesMissing()) {
				continue;
			}
			probes = dataPreprocessor.preprocess(probes, node);

			/* at this point only 1 branch is missing at most */
			nodeProbe = probes.getNodeProbe(node);
			CoveredBranches coveredType = nodeProbe.getCoveredBranches();
			TrueFalseLearningResult trueFalseResult = generateTrueFalseFormula(nodeProbe, coveredType);
			Formula oneMore = generateLoopFormula(nodeProbe);
			nodeProbe.setPrecondition(Pair.of(trueFalseResult.formula, oneMore), trueFalseResult.dividers);
		}
	}

	private static final int TRUE_FALSE_LEARN_MAX_ATTEMPT = 5;
	private TrueFalseLearningResult generateTrueFalseFormula(DecisionNodeProbe orgNodeProbe, CoveredBranches coveredType) throws SAVExecutionTimeOutException {
		/* only generate if both branches are covered */
		if (coveredType != CoveredBranches.TRUE_AND_FALSE || !orgNodeProbe.doesNodeNeedToLearnPrecond()) {
			return null;
		}
		Formula trueFlaseFormula = null;
		/* do generate formula and return */
		NegativePointSelection negative = new ByDistanceNegativePointSelection();
		PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);
		trueFlaseFormula = generateInitialFormula(orgNodeProbe, mcm);
		System.currentTimeMillis();
		double acc = mcm.getModelAccuracy();
		List<Divider> dividers = mcm.getLearnedDividers();
		System.out.println("=============learned multiple cut: " + trueFlaseFormula);

		int time = 0;
		DecisionNodeProbe nodeProbe = orgNodeProbe;
		CfgNode node = nodeProbe.getNode();
		while (trueFlaseFormula != null && time < TRUE_FALSE_LEARN_MAX_ATTEMPT
				&& nodeProbe.doesNodeNeedToLearnPrecond()) {
			long startTime = System.currentTimeMillis();
			DecisionProbes probes = nodeProbe.getDecisionProbes();
			nodeProbe = dataPreprocessor.processData(probes, node).getNodeProbe(node);
		
			/* TODO LLT: to adapt (as in old implementation, input data is newBreakpointData, check whether it includes all or only new ones)*/
			nodeProbe.getPreconditions().clearInvalidData(nodeProbe);
			mcm.getLearnedModels().clear();
			addDataPoints(probes.getLabels(), probes.getOriginalVars(), nodeProbe.getTrueValues(), Category.POSITIVE, mcm);
			addDataPoints(probes.getLabels(), probes.getOriginalVars(), nodeProbe.getFalseValues(), Category.NEGATIVE, mcm);
			System.out.println("true data after selective sampling" + nodeProbe.getTrueValues());
			System.out.println("false data after selective sampling" + nodeProbe.getFalseValues());

			mcm.train();
			Formula tmp = mcm.getLearnedMultiFormula(probes.getOriginalVars(), probes.getLabels());
			System.out.println("improved the formula: " + tmp);
			if (tmp == null) {
				break;
			}

			double accTmp = mcm.getModelAccuracy();
			acc = mcm.getModelAccuracy();
			if (!tmp.equals(trueFlaseFormula)) {
				trueFlaseFormula = tmp;
				dividers = mcm.getLearnedDividers();
				acc = accTmp;

				if (acc == 1.0) {
					break;
				}
			} else {
				break;
			}

			time++;
		}
		TrueFalseLearningResult result = new TrueFalseLearningResult();
		result.formula = trueFlaseFormula;
		result.dividers = dividers;
		return result;
	}
	
	private Formula generateInitialFormula(DecisionNodeProbe nodeProbe, PositiveSeparationMachine mcm)
			throws SAVExecutionTimeOutException {
		DecisionProbes probes = nodeProbe.getDecisionProbes();
		mcm.setDefaultParams();
		List<String> labels = probes.getLabels();
		mcm.setDataLabels(labels);
		mcm.setDefaultParams();
		for(BreakpointValue value: nodeProbe.getTrueValues()){
			addDataPoint(labels, probes.getOriginalVars(), value, Category.POSITIVE, mcm);
		}
		for(BreakpointValue value: nodeProbe.getFalseValues()){
			addDataPoint(labels, probes.getOriginalVars(), value, Category.NEGATIVE, mcm);
		}
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(probes.getOriginalVars(), labels);
		
		return newFormula;
	}
	
	private void addDataPoints(List<String> labels, List<ExecVar> vars, List<BreakpointValue> values, Category category, Machine machine) {
		for (BreakpointValue value : values) {
			addDataPoint(labels, vars, value, category, machine);
		}
	}
	
	private void addDataPoint(List<String> labels, List<ExecVar> vars, BreakpointValue bValue, Category category, Machine machine) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		for (ExecVar var : vars) {
			final Double value = bValue.getValue(var.getLabel(), 0.0);
			lineVals[i++] = value;
		}
		int size = vars.size();
		for (int j = 0; j < size; j++) {
//			double value = bValue.getValue(vars.get(j).getLabel(), 0.0);
			for (int k = j; k < size; k++) {
//				lineVals[i ++] = value * bValue.getValue(vars.get(k).getLabel(), 0.0);
				lineVals[i ++] = 0.0;
			}
		}

		machine.addDataPoint(category, lineVals);
	}

	/**
	 * @param nodeProbe
	 */
	private void recordTestInput(DecisionNodeProbe nodeProbe) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param nodeProbe
	 * @return
	 */
	private Formula generateLoopFormula(DecisionNodeProbe nodeProbe) {
		if (!nodeProbe.getNode().isLoopHeader()) {
			return null;
		}
		/* TODO LLT: to solve random flag */
		OrCategoryCalculator preconditions = nodeProbe.getPreconditions();
		DecisionProbes probes = dataPreprocessor.onBeforeLearningLoop(nodeProbe.getDecisionProbes(), nodeProbe.getNode());
		/* after trying to prepare data */
		if (nodeProbe.getOneTimeValues().isEmpty()) {
			log.info("Missing once loop data");
			return null;
		}
		
		
		return null;
	}
	
	private static class TrueFalseLearningResult {
		Formula formula;
		List<Divider> dividers;
	}
}
