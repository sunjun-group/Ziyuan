/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.AbstractLearningComponent;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.CfgUtils;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.IlpSelectiveSampling;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT different from DecisionLearner which does sampling randomly, this
 *         learner using precondition (which is built based on classifier of
 *         node's dominatees) for sampling.
 */
public class PrecondDecisionLearner extends AbstractLearningComponent implements IInputLearner {
	private static Logger log = LoggerFactory.getLogger(PrecondDecisionLearner.class);
	private static int FORMULAR_LEARN_MAX_ATTEMPT = 5;
	protected LearnedDataProcessor dataPreprocessor;
	public HashMap<CfgNode, FormulaInfo> learnedFormulas = new HashMap<>();
	private HashMap<CfgNode, CfgNodeDomainInfo> dominationMap = new HashMap<>();
	HashMap<String, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(), branchFalseRecord = new HashMap<>();

	public PrecondDecisionLearner(LearningMediator mediator) {
		super(mediator);
	}

	public DecisionProbes learn(DecisionProbes inputProbes, BreakpointData bpdata) throws SavException {
		List<CfgNode> decisionNodes = inputProbes.getCfg().getDecisionNodes();
		DecisionProbes probes = inputProbes;
		dataPreprocessor = new LearnedDataProcessor(mediator, inputProbes);
		dominationMap = new CfgDomain().constructDominationMap(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()));
		recordSample(inputProbes, dataPreprocessor.getSampleOfInitCase(bpdata, inputProbes.getOriginalVars()));
		learn(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()), probes, new ArrayList<Integer>(decisionNodes.size()));
		return probes;
	}

	

	private void learn(CfgNode node, DecisionProbes probes, List<Integer> visitedNodes) throws SavException {
		if (probes.getOriginalVars().size()>50) { /** discard those method with too many variables */
			log.debug("OriginalVars size is "+probes.getOriginalVars().size() + " > 50, return directly");
			return;
		}
		log.debug("parsing the node in line " + node.getLine() + "(" + node + ")");

		// for (CfgNode dominator : CfgUtils.getPrecondInherentDominatee(node))
		// { /** update dominator node's condition*/
		// if (!visitedNodes.contains(dominator.getIdx())) {
		// learn(dominator, probes, visitedNodes);
		// }
		// }
		DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
		if (!needToLearn(nodeProbe)) {
			visitedNodes.add(node.getIdx());
			return;
		}
		log.debug("learning the node in line " + node.getLine() + "(" + node + ")");
		OrCategoryCalculator preconditions = getPreconditions(probes, node);
		dataPreprocessor.sampleForBranchCvg(node, preconditions, this);
		dataPreprocessor.sampleForLoopCvg(node, preconditions, this);

		nodeProbe = probes.getNodeProbe(node);

		updatePrecondition(nodeProbe);

		visitedNodes.add(node.getIdx());
		List<CfgNode> childDecisonNodes = dominationMap.get(node).dominatees;
		for (CfgNode dependentee : CollectionUtils.nullToEmpty(childDecisonNodes)) {
			if (null != dependentee && !visitedNodes.contains(dependentee.getIdx())) {
				learn(dependentee, probes, visitedNodes);
			}
		}
	}

	private boolean needToLearn(DecisionNodeProbe nodeProbe) {
		return !nodeProbe.areAllbranchesUncovered() || nodeProbe.needToLearnPrecond();
	}

	protected void updatePrecondition(DecisionNodeProbe nodeProbe) throws SavException {
		/* at this point only 1 branch is missing at most */
		CoveredBranches coveredType = nodeProbe.getCoveredBranches();
		TrueFalseLearningResult trueFalseResult = generateTrueFalseFormula(nodeProbe, coveredType);
//		Formula oneMore = generateLoopFormula(nodeProbe);
		Formula truefalseFormula = trueFalseResult == null ? null : trueFalseResult.formula;
		List<Divider> divider = trueFalseResult == null ? null : trueFalseResult.dividers;
		nodeProbe.setPrecondition(Pair.of(truefalseFormula, null), divider);
		nodeProbe.clearCache();
		
		System.out.println("final formula : "+ truefalseFormula);
	}

	protected OrCategoryCalculator getPreconditions(DecisionProbes probes, CfgNode node) {
		return probes.getPrecondition(node);
	}

	private TrueFalseLearningResult generateTrueFalseFormula(DecisionNodeProbe orgNodeProbe,
			CoveredBranches coveredType) throws SavException {
		System.currentTimeMillis();
		if (!orgNodeProbe.needToLearnPrecond()) {
			log.debug("no need to learn precondition");
			return null;
		}
		log.debug("generate true false formula..");
		/* only generate if both branches are covered */
		if (coveredType != CoveredBranches.TRUE_AND_FALSE) {
			log.debug("Only branch {} is covered!", coveredType);
			return null;
		}
		Formula trueFlaseFormula = null;

		/* do generate formula and return */
		NegativePointSelection negative = new ByDistanceNegativePointSelection();
		PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);
		trueFlaseFormula = generateInitialFormula(orgNodeProbe, mcm);
		double acc = mcm.getModelAccuracy();

		List<Divider> dividers = mcm.getLearnedDividers();
		log.info("=============learned multiple cut: " + trueFlaseFormula);

		int time = 0;
		DecisionNodeProbe nodeProbe = orgNodeProbe;
		CfgNode node = nodeProbe.getNode();

		if (trueFlaseFormula != null && (!(time < FORMULAR_LEARN_MAX_ATTEMPT) || !nodeProbe.needToLearnPrecond())) {
			/** record learned formulas */
			if (!learnedFormulas.containsKey(node)) {
				learnedFormulas.put(node, new FormulaInfo(node));
			}
			learnedFormulas.get(node).addTFFormula(trueFlaseFormula.toString(), acc);
		}

		while (trueFlaseFormula != null && time < FORMULAR_LEARN_MAX_ATTEMPT && nodeProbe.needToLearnPrecond()) {

			/** record learned formulas */
			if (!learnedFormulas.containsKey(node)) {
				learnedFormulas.put(node, new FormulaInfo(node));
			}
			learnedFormulas.get(node).addTFFormula(trueFlaseFormula.toString(), acc);

			IlpSelectiveSampling.iterationTime = FORMULAR_LEARN_MAX_ATTEMPT - time;
			time++;
			DecisionProbes probes = nodeProbe.getDecisionProbes();
			log.debug("selective sampling: ");
			log.debug("original vars: size={}, {}", probes.getOriginalVars().size(), probes.getOriginalVars());
			/* after running sampling, probes will be updated as well */
			SamplingResult sampleResult = dataPreprocessor.sampleForModel(nodeProbe, probes.getOriginalVars(),
					getPreconditions(probes, node), mcm.getLearnedDividers());
			if (sampleResult == null) {
				log.debug("sampling result is null");
				continue;
			}
			INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
			nodeProbe.getPreconditions().clearInvalidData(newData);
			mcm.getLearnedModels().clear();
			addDataPoint(probes.getLabels(), probes.getOriginalVars(), 
					newData.getTrueValues(), newData.getFalseValues(), mcm);
			recordSample(probes, sampleResult);
			System.out.println(mcm.getDataPoints().size());
			mcm.train();
			Formula tmp = mcm.getLearnedMultiFormula(probes.getOriginalVars(), probes.getLabels());
			log.info("improved the formula: " + tmp);
			if (tmp == null) {
				break;
			}

			double accTmp = mcm.getModelAccuracy();
			acc = mcm.getModelAccuracy();
			if (!tmp.equals(trueFlaseFormula)) {
				trueFlaseFormula = tmp;
				dividers = mcm.getLearnedDividers();
				acc = accTmp;

//				 if (acc == 1.0) {
//				 break;
//				 }
			} else {
				break;
			}

		}
		if (trueFlaseFormula != null && acc < 0.5) {
			return null;
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
		addDataPoint(labels, probes.getOriginalVars(), 
				nodeProbe.getTrueValues(), nodeProbe.getFalseValues(), mcm);
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(probes.getOriginalVars(), labels);

		// for(DataPoint point: mcm.getDataPoints()){
		// System.out.println(point);
		// }

		return newFormula;
	}

	private void addDataPoint(List<String> labels, List<ExecVar> vars, Collection<BreakpointValue> trueV, Collection<BreakpointValue> falseV,
			PositiveSeparationMachine mcm) {
		for (BreakpointValue value : trueV) {
			addBkp(labels, vars, value, Category.POSITIVE, mcm);
		}
		for (BreakpointValue value : falseV) {
			addBkp(labels, vars, value, Category.NEGATIVE, mcm);
		}
		
	}

	private void addBkp(List<String> labels, List<ExecVar> vars, BreakpointValue bValue, Category category,
			Machine machine) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		for (ExecVar var : vars) {
			final Double value = bValue.getValue(var.getLabel(), 0.0);
			lineVals[i++] = value;
		}
		int size = vars.size();
		for (int j = 0; j < size; j++) {
			// double value = bValue.getValue(vars.get(j).getLabel(), 0.0);
			for (int k = j + 1; k < size; k++) {
				// lineVals[i ++] = value *
				// bValue.getValue(vars.get(k).getLabel(), 0.0);
				lineVals[i++] = 0.0;
			}
		}

		machine.addDataPoint(category, lineVals);
	}

//	private Formula generateLoopFormula(DecisionNodeProbe nodeProbe) throws SavException {
//		if (!nodeProbe.getNode().isLoopHeader() || !nodeProbe.getCoveredBranches().coversTrue()) {
//			return null;
//		}
//		log.debug("generate loop formula..");
//		System.currentTimeMillis();
//		if (nodeProbe.getOneTimeValues().isEmpty() || nodeProbe.getMoreTimesValues().isEmpty()) {
//			log.info("Missing once loop data");
//			return null;
//		} else if (nodeProbe.getMoreTimesValues().isEmpty()) {
//			log.info("Missing more than once loop data");
//			return null;
//		}
//		return generateConcreteLoopFormula(nodeProbe);
//	}

//	private Formula generateConcreteLoopFormula(DecisionNodeProbe nodeProbe) throws SavException {
//		Formula formula = null;
//		if (nodeProbe.needToLearnPrecond()) {
//			NegativePointSelection negative = new ByDistanceNegativePointSelection();
//			PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);
//			formula = generateInitialFormula(nodeProbe, mcm);
//
//			int times = 0;
//			double acc = mcm.getModelAccuracy();
//			List<ExecVar> originalVars = nodeProbe.getDecisionProbes().getOriginalVars();
//			List<String> labels = nodeProbe.getDecisionProbes().getLabels();
//			while (formula != null && times < FORMULAR_LEARN_MAX_ATTEMPT && nodeProbe.needToLearnPrecond()) {
//
//				/** record learned formulas */
//				CfgNode node = nodeProbe.getNode();
//				if (!learnedFormulas.containsKey(node)) {
//					learnedFormulas.put(node, new FormulaInfo(node));
//				}
//				learnedFormulas.get(node).addLoopFormula(formula.toString(), acc);
//
//				SamplingResult samples = dataPreprocessor.sampleForModel(nodeProbe, originalVars,
//						nodeProbe.getPreconditions(), mcm.getLearnedDividers());
//				INodeCoveredData newData = samples.getNewData(nodeProbe);
//				addDataPoints(labels, originalVars, newData.getMoreTimesValues(), Category.POSITIVE, mcm);
//				addDataPoints(labels, originalVars, newData.getOneTimeValues(), Category.NEGATIVE, mcm);
//				recordSample(nodeProbe.getDecisionProbes(), samples);
//				acc = mcm.getModelAccuracy();
//				if (acc == 1.0) {
//					break;
//				}
//				mcm.train();
//				Formula tmp = mcm.getLearnedMultiFormula(originalVars, labels);
//
//				double accTmp = mcm.getModelAccuracy();
//				// if (tmp == null) {
//				// break;
//				// }
//				if (!tmp.equals(formula) && accTmp > acc) {
//					formula = tmp;
//					acc = accTmp;
//				} else {
//					break;
//				}
//				times++;
//			}
//			
//			if (formula!=null && acc < 0.5) {
//				formula = null;
//			}
//
//		}
//
//		return formula;
//	}

	public boolean isUsingPrecondApproache() {
		return true;
	}

	private static class TrueFalseLearningResult {
		Formula formula;
		List<Divider> dividers;
	}

	public HashMap<String, Collection<BreakpointValue>> getTrueSample(){
		return branchTrueRecord;
	}
	
	public HashMap<String, Collection<BreakpointValue>> getFalseSample(){
		return branchFalseRecord;
	}

	public HashMap<CfgNode, CfgNodeDomainInfo> getDominationMap() {
		return dominationMap;
	}
	
	
}
