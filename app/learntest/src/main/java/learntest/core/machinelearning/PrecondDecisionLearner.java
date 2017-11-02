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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.texteditor.InsertLineAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.AbstractLearningComponent;
import learntest.core.LearningMediator;
import learntest.core.RunTimeInfo;
import learntest.core.TestRunTimeInfo;
import learntest.core.Visitor;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.CfgUtils;
import learntest.core.commons.utils.VariableUtils;
import learntest.core.commons.utils.VariableUtils.VarInfo;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.IlpSelectiveSampling;
import learntest.core.rule.EqualVarRelationShip;
import learntest.core.rule.NotEqualVarRelationShip;
import learntest.core.rule.RelationShip;
import learntest.plugin.utils.Settings;
import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Model;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.formula.Operator;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import variable.Variable;

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

	HashMap<String, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(),
			branchFalseRecord = new HashMap<>();
	List<VarInfo> relevantVars;
	private String logFile;

	private CompilationUnit cu;

	public PrecondDecisionLearner(LearningMediator mediator, String logFile) {
		super(mediator);
		this.logFile = logFile;
		RunTimeInfo.createFile(logFile);
	}

	public DecisionProbes learn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap)
			throws SavException {
		List<CfgNode> decisionNodes = inputProbes.getCfg().getDecisionNodes();
		DecisionProbes probes = inputProbes;
		dataPreprocessor = new LearnedDataProcessor(mediator, inputProbes);
		dominationMap = new CfgDomain().constructDominationMap(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()));

		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("dominationMap : \n");
		for (CfgNodeDomainInfo info : dominationMap.values()) {
			if (!info.dominatees.isEmpty()) {
				sBuffer.append(info + "\n\n");
			}
		}
		FileUtils.write(logFile, sBuffer.toString());
		log.info(sBuffer.toString());
		if (relevantVarMap == null || probes.getCfg().getNodeList().size() != relevantVarMap.size()) {
			log.debug("The size of CfgNodes is differnt from the size of map!!!!");
			this.relevantVars = null;
		} else {
			this.relevantVars = VariableUtils.varsTransform(relevantVarMap, inputProbes.getOriginalVars());
		}
		
		HashMap<Integer, List<CfgNode>> indexMap = new HashMap<>();
		for (CfgNode cfgNode : decisionNodes) {
			int line = cfgNode.getLine();
			if (indexMap.containsKey(line)) {
				indexMap.get(line).add(cfgNode);
			}else {
				List<CfgNode> list = new LinkedList<>();
				list.add(cfgNode);
				indexMap.put(line, list);
			}
		}
		learn(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()), probes, new ArrayList<Integer>(decisionNodes.size()), indexMap);
		return probes;
	}

	private void learn(CfgNode node, DecisionProbes probes, List<Integer> visitedNodes, HashMap<Integer, List<CfgNode>> indexMap) throws SavException {
		
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(node);
		int loopTimes = 0;
		while (!queue.isEmpty()) {
			loopTimes++;
			node = queue.poll();
			log.debug("parsing the node in line " + node.getLine() + "(" + node + ")");
			
			// for (CfgNode dominator :
			// CfgUtils.getPrecondInherentDominatee(node))
			// { /** update dominator node's condition*/
			// if (!visitedNodes.contains(dominator.getIdx())) {
			// learn(dominator, probes, visitedNodes);
			// }
			// }
			DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
			if (needToLearn(nodeProbe)) {

				List<ExecVar> targetVars;
				relevantVars = null;
				if (relevantVars != null) {
					targetVars = relevantVars.get(node.getIdx()).getExecVars();
				} else {
					targetVars = probes.getOriginalVars();
				}

				if (targetVars.size() > 50) { /**
												 * discard those method with too
												 * many variables
												 */
					log.debug("targetVars size is " + targetVars.size() + " > 50, return directly!!!");
					return;
				}

				Pair<OrCategoryCalculator, Boolean> pair = null;
				log.debug("learning the node in line " + node.getLine() + "(" + node + ")");
				if (loopTimes < 100 ? node.isLoopHeader() : node.isInLoop()) { // give a simple patch when there is a bug that will cause infinite loop
					/**
					 * todo : handle the loop loop header dominate and is
					 * dominated by nodes in loop, thus in order to break the
					 * wait lock, loop header should be learned first without
					 * learing nodes in loop
					 */
					pair = getPreconditions(probes, node, true);
				} else {
					pair = getPreconditions(probes, node, false);
				}

				if (!pair.second()) {
					log.debug("to learn the node in line " + node.getLine() + "(" + node
							+ "), its dominator has to be learned before");
					queue.add(node);
					continue;
				}
				log.debug("relevant vars : " + targetVars);
				OrCategoryCalculator preconditions = pair.first();
				// dataPreprocessor.sampleForBranchCvg(node, preconditions, this);
				// dataPreprocessor.sampleForLoopCvg(node, preconditions, this);
				RelationShip relationShip = null;
				if (indexMap.containsKey(node.getLine())) {
					int index = indexMap.get(node.getLine()).indexOf(node);
					Visitor visitor = new Visitor(node.getLine(), cu, index);
					cu.accept(visitor);
					relationShip = visitor.getRelationShip();
				}
				if ( (relationShip = getHeuristicRule(nodeProbe, cu)) != null) { // heuristic rules help to get condition directly
					updatePrecondition(nodeProbe, relationShip);
					
				} else {
					if (!dataPreprocessor.sampleForMissingBranch(node, this)) {
						dataPreprocessor.sampleForBranchCvg(node, preconditions, this);
					}

					updatePrecondition(nodeProbe, preconditions, targetVars);
				}

				nodeProbe.getPrecondition().setVisited(true);
			} else {
				nodeProbe.getPrecondition().setVisited(true);
			}

			visitedNodes.add(node.getIdx());
			List<CfgNode> childDecisonNodes = dominationMap.get(node).getDominatees();

			/**
			 * handle the situation that first branch node does not dominate
			 * fellow branch node
			 */
			for (CfgNode cfgNode : DecisionProbes.getChildDecision(node)) {
				if (!childDecisonNodes.contains(cfgNode) && !visitedNodes.contains(cfgNode.getIdx())) {
					childDecisonNodes.add(cfgNode);
				}
			}

			childDecisonNodes.sort(new DomainationComparator(dominationMap));
			for (CfgNode dependentee : CollectionUtils.nullToEmpty(childDecisonNodes)) {
				if (null != dependentee && !visitedNodes.contains(dependentee.getIdx())
						&& !queue.contains(dependentee)) {
					queue.add(dependentee);
				}
			}

			if (queue.isEmpty()) {
				checkLearnedComplete(visitedNodes, probes.getCfg().getDecisionNodes(), queue);
			}
		}
	}

	private EqualVarRelationShip getHeuristicRule(DecisionNodeProbe nodeProbe, CompilationUnit cu2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * check if all decision nodes are learned, otherwise poll all
	 * 
	 * @param visitedNodes
	 * @param decisionNodes
	 * @param queue
	 */
	private void checkLearnedComplete(List<Integer> visitedNodes, List<CfgNode> decisionNodes, Queue<CfgNode> queue) {
		for (CfgNode cfgNode : decisionNodes) {
			if (!visitedNodes.contains(cfgNode.getIdx())) {
				queue.add(cfgNode);
				log.debug("this node is missed : " + cfgNode);
			}
		}

	}

	private boolean needToLearn(DecisionNodeProbe nodeProbe) {
		// return !nodeProbe.areAllbranchesUncovered() ||
		// nodeProbe.needToLearnPrecond();

		if (!nodeProbe.areAllbranchesUncovered()) {
			log.debug("All branches are uncovered!");
			return true;
		} else {
			DecisionProbes probes = nodeProbe.getDecisionProbes();
			for (CfgNode dependentee : dominationMap.get(nodeProbe.getNode()).getDominatees()) {
				DecisionNodeProbe dependenteeProbe = probes.getNodeProbe(dependentee);
				if (dependenteeProbe.hasUncoveredBranch()) {
					return true;
				}
			}
			return false;
		}
	}

	protected void updatePrecondition(DecisionNodeProbe nodeProbe, OrCategoryCalculator preconditions,
			List<ExecVar> targetVars) throws SavException {
		/* at this point only 1 branch is missing at most */
		CoveredBranches coveredType = nodeProbe.getCoveredBranches();
		TrueFalseLearningResult trueFalseResult = generateTrueFalseFormula(nodeProbe, coveredType, preconditions,
				targetVars);
		// Formula oneMore = generateLoopFormula(nodeProbe);
		Formula truefalseFormula = trueFalseResult == null ? null : trueFalseResult.formula;
		List<Divider> divider = trueFalseResult == null ? null : trueFalseResult.dividers;
		nodeProbe.setPrecondition(Pair.of(truefalseFormula, null), divider);
		nodeProbe.clearCache();

		log.info("final formula : " + truefalseFormula);
	}
	
	protected void updatePrecondition(DecisionNodeProbe nodeProbe,	RelationShip relationShip) throws SavException {
		
		TrueFalseLearningResult trueFalseResult = generateTrueFalseFormulaByHerustic(nodeProbe.getDecisionProbes().getOriginalVars(), relationShip);
		Formula truefalseFormula = trueFalseResult == null ? null : trueFalseResult.formula;
		List<Divider> divider = trueFalseResult == null ? null : trueFalseResult.dividers;
		nodeProbe.setPrecondition(Pair.of(truefalseFormula, null), divider);
		nodeProbe.clearCache();
		log.info("final formula : " + truefalseFormula);
	}

	protected Pair<OrCategoryCalculator, Boolean> getPreconditions(DecisionProbes probes, CfgNode node,
			boolean isLoopHeader) {
		return probes.getPrecondition(node, dominationMap, isLoopHeader);
	}

	private TrueFalseLearningResult generateTrueFalseFormula(DecisionNodeProbe orgNodeProbe,
			CoveredBranches coveredType, OrCategoryCalculator preconditions, List<ExecVar> targetVars)
					throws SavException {

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
		List<ExecVar> orignalVars = orgNodeProbe.getDecisionProbes().getOriginalVars();

		/* do generate formula and return */
		NegativePointSelection negative = new ByDistanceNegativePointSelection();
		PositiveSeparationMachine mcm = new LearningMachine(negative);
		log.info("generate initial formula");
		trueFlaseFormula = generateInitialFormula(orgNodeProbe, mcm, targetVars);
		double acc = mcm.getModelAccuracy();
		if (mcm.getDataPoints().size() <= 1) {
			log.info("there is only one data point !!! svm could not learn");
			return null;
		}
		List<Divider> dividers = mcm.getFullLearnedDividers(mcm.getDataLabels(), orignalVars);
		log.info("=============learned multiple cut: " + trueFlaseFormula);

		int time = 0;
		DecisionNodeProbe nodeProbe = orgNodeProbe;
		CfgNode node = nodeProbe.getNode();

		while (trueFlaseFormula != null && time < FORMULAR_LEARN_MAX_ATTEMPT && nodeProbe.needToLearnPrecond()) {

			/** record learned formulas */
			if (!learnedFormulas.containsKey(node)) {
				learnedFormulas.put(node, new FormulaInfo(node));
			}
			learnedFormulas.get(node).addTFFormula(trueFlaseFormula.toString(), acc);

			IlpSelectiveSampling.iterationTime = FORMULAR_LEARN_MAX_ATTEMPT - time;
			time++;
			DecisionProbes probes = nodeProbe.getDecisionProbes();
			System.currentTimeMillis();
			log.debug("selective sampling: ");
			log.debug("original vars: {}, targetVars : {}", probes.getOriginalVars(), targetVars);
			/* after running sampling, probes will be updated as well */
			SamplingResult sampleResult = dataPreprocessor.sampleForModel(nodeProbe, orignalVars, preconditions,
					mcm.getFullLearnedDividers(mcm.getDataLabels(), orignalVars), logFile);
			if (sampleResult == null) {
				log.debug("sampling result is null");
				continue;
			}
			INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
			nodeProbe.getPreconditions(preconditions).clearInvalidData(newData);
			mcm.getLearnedModels().clear();
			addDataPoint(mcm.getDataLabels(), targetVars, newData.getTrueValues(), newData.getFalseValues(), mcm);
			recordSample(probes, sampleResult, logFile);

			mcm.train();
			Formula tmp = mcm.getLearnedMultiFormula(targetVars, mcm.getDataLabels());
			log.info("improved the formula: " + tmp);
			if (tmp == null) {
				break;
			}

			double accTmp = mcm.getModelAccuracy();
			acc = mcm.getModelAccuracy();
			if (!tmp.equals(trueFlaseFormula)) {
				trueFlaseFormula = tmp;
				dividers = mcm.getFullLearnedDividers(mcm.getDataLabels(), probes.getOriginalVars());
				acc = accTmp;
			} else {
				break;
			}

		}

		if (trueFlaseFormula != null) {
			/** record learned formulas */
			if (!learnedFormulas.containsKey(node)) {
				learnedFormulas.put(node, new FormulaInfo(node));
			}
			learnedFormulas.get(node).addTFFormula(trueFlaseFormula.toString(), acc);
			if (acc < Settings.formulaAccThreshold) {
				return null;
			}
		}
		TrueFalseLearningResult result = new TrueFalseLearningResult();
		result.formula = trueFlaseFormula;
		result.dividers = dividers;
		return result;
	}

	private Formula generateInitialFormula(DecisionNodeProbe nodeProbe, PositiveSeparationMachine mcm,
			List<ExecVar> targetVars) throws SAVExecutionTimeOutException {
		// DecisionProbes probes = nodeProbe.getDecisionProbes();
		// mcm.setDefaultParams();
		// List<String> labels = probes.getLabels();
		// mcm.setDataLabels(labels);
		// mcm.setDefaultParams();
		// addDataPoint(labels, probes.getOriginalVars(), targetVars,
		// nodeProbe.getTrueValues(), nodeProbe.getFalseValues(), mcm);
		// mcm.train();
		// Formula newFormula =
		// mcm.getLearnedMultiFormula(probes.getOriginalVars(), labels);

		mcm.setDefaultParams();

		List<String> labels = new LinkedList<>();
		for (ExecVar var : targetVars) {
			labels.add(var.getLabel());
		}

		mcm.setDataLabels(labels);
		mcm.setDefaultParams();
		addDataPoint(mcm.getDataLabels(), targetVars, nodeProbe.getTrueValues(), nodeProbe.getFalseValues(), mcm);
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(targetVars, labels);

		return newFormula;
	}

	private TrueFalseLearningResult generateTrueFalseFormulaByHerustic(List<ExecVar> originalVars, RelationShip relationShip) throws SavException {
		
		List<String> labels = new LinkedList<>();
		for (ExecVar var : originalVars) {
			labels.add(var.getLabel());
		}
		List<Divider> dividers = constructDividers(labels, relationShip);

		Formula trueFlaseFormula = null;
		if (relationShip instanceof EqualVarRelationShip) {
			for (Divider divider : dividers) {
				Formula current = new FormulaProcessor<ExecVar>(originalVars).process(divider, labels, true);
				if (trueFlaseFormula == null) {
					trueFlaseFormula = current;
				} else {
					trueFlaseFormula = new AndFormula(trueFlaseFormula, current);
				}
			}
		}else if (relationShip instanceof NotEqualVarRelationShip) {
			trueFlaseFormula = new FormulaProcessor<ExecVar>(originalVars).process(dividers.get(0), labels, true, Operator.NE);
		}

		TrueFalseLearningResult result = new TrueFalseLearningResult();
		result.formula = trueFlaseFormula;
		result.dividers = dividers;
		return result;
	}

	private List<Divider> constructDividers(List<String> labels, RelationShip relationShip) {
		List<Divider> dividers = new LinkedList<>();
		if (relationShip instanceof EqualVarRelationShip || relationShip instanceof NotEqualVarRelationShip) {
			double[] result1 = new double[labels.size()], result2 = new double[labels.size()];
			String left = ((EqualVarRelationShip)relationShip).getLeft(), right = ((EqualVarRelationShip)relationShip).getRight();
			int indexL = 0, indexR = 0;
			for (int i = 0; i < labels.size(); i++) {
				if (labels.get(i).equals(left)) {
					indexL = i;
				}else if (labels.get(i).equals(right)) {
					indexR = i;
				}
			}
			result1[indexL] = 1;
			result1[indexR] = -1;
			result2[indexL] = -1;
			result2[indexR] = 1;
			
			Divider divider1 = new Divider(result1, 0), divider2 = new Divider(result2, 0);
			dividers.add(divider1);
			dividers.add(divider2);
		}
		return dividers;
	}

	private void addDataPoint(List<String> labels, List<ExecVar> targetVars, Collection<BreakpointValue> trueV,
			Collection<BreakpointValue> falseV, PositiveSeparationMachine mcm) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("add data point to svm : =============================\n");
		sBuffer.append("new data true : " + trueV.size() + " , " + trueV.toString() + "\n");
		for (BreakpointValue value : trueV) {
			addBkp(labels, targetVars, value, Category.POSITIVE, mcm, sBuffer);
		}
		sBuffer.append("new data false : " + falseV.size() + " , " + falseV.toString() + "\n");
		for (BreakpointValue value : falseV) {
			addBkp(labels, targetVars, value, Category.NEGATIVE, mcm, sBuffer);
		}
		log.info(sBuffer.toString());
		// log.info("new data true : "+trueV.size()+" , "+trueV.toString());
		// log.info("new data false : "+falseV.size()+" , "+falseV.toString());

		FileUtils.write(logFile, sBuffer.toString());
	}

	private void addBkp(List<String> labels, List<ExecVar> targetVars, BreakpointValue bValue, Category category,
			Machine machine, StringBuffer sBuffer) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		sBuffer.append("(");
		for (ExecVar var : targetVars) {
			final Double value;
			value = bValue.getValue(var.getLabel(), 0.0);
			lineVals[i++] = value;
			sBuffer.append(var.getLabel() + ":" + value + ", ");
		}
		/** below is about derived vars like x*y */
		// if (i < labels.size()) {
		// int size = targetVars.size();
		// for (int j = 0; j < size; j++) {
		// double value = bValue.getValue(targetVars.get(j).getLabel(), 0.0);
		// for (int k = j + 1; k < size; k++) {
		// lineVals[i ++] = value *
		// bValue.getValue(targetVars.get(k).getLabel(), 0.0);
		// }
		// }
		// }
		sBuffer.append(")\n");
		machine.addDataPoint(category, lineVals);
	}

	// private Formula generateLoopFormula(DecisionNodeProbe nodeProbe) throws
	// SavException {
	// if (!nodeProbe.getNode().isLoopHeader() ||
	// !nodeProbe.getCoveredBranches().coversTrue()) {
	// return null;
	// }
	// log.debug("generate loop formula..");
	// System.currentTimeMillis();
	// if (nodeProbe.getOneTimeValues().isEmpty() ||
	// nodeProbe.getMoreTimesValues().isEmpty()) {
	// log.info("Missing once loop data");
	// return null;
	// } else if (nodeProbe.getMoreTimesValues().isEmpty()) {
	// log.info("Missing more than once loop data");
	// return null;
	// }
	// return generateConcreteLoopFormula(nodeProbe);
	// }

	// private Formula generateConcreteLoopFormula(DecisionNodeProbe nodeProbe)
	// throws SavException {
	// Formula formula = null;
	// if (nodeProbe.needToLearnPrecond()) {
	// NegativePointSelection negative = new ByDistanceNegativePointSelection();
	// PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);
	// formula = generateInitialFormula(nodeProbe, mcm);
	//
	// int times = 0;
	// double acc = mcm.getModelAccuracy();
	// List<ExecVar> originalVars =
	// nodeProbe.getDecisionProbes().getOriginalVars();
	// List<String> labels = nodeProbe.getDecisionProbes().getLabels();
	// while (formula != null && times < FORMULAR_LEARN_MAX_ATTEMPT &&
	// nodeProbe.needToLearnPrecond()) {
	//
	// /** record learned formulas */
	// CfgNode node = nodeProbe.getNode();
	// if (!learnedFormulas.containsKey(node)) {
	// learnedFormulas.put(node, new FormulaInfo(node));
	// }
	// learnedFormulas.get(node).addLoopFormula(formula.toString(), acc);
	//
	// SamplingResult samples = dataPreprocessor.sampleForModel(nodeProbe,
	// originalVars,
	// nodeProbe.getPreconditions(), mcm.getLearnedDividers());
	// INodeCoveredData newData = samples.getNewData(nodeProbe);
	// addDataPoints(labels, originalVars, newData.getMoreTimesValues(),
	// Category.POSITIVE, mcm);
	// addDataPoints(labels, originalVars, newData.getOneTimeValues(),
	// Category.NEGATIVE, mcm);
	// recordSample(nodeProbe.getDecisionProbes(), samples);
	// acc = mcm.getModelAccuracy();
	// if (acc == 1.0) {
	// break;
	// }
	// mcm.train();
	// Formula tmp = mcm.getLearnedMultiFormula(originalVars, labels);
	//
	// double accTmp = mcm.getModelAccuracy();
	// // if (tmp == null) {
	// // break;
	// // }
	// if (!tmp.equals(formula) && accTmp > acc) {
	// formula = tmp;
	// acc = accTmp;
	// } else {
	// break;
	// }
	// times++;
	// }
	//
	// if (formula!=null && acc < 0.5) {
	// formula = null;
	// }
	//
	// }
	//
	// return formula;
	// }

	public boolean isUsingPrecondApproache() {
		return true;
	}

	private static class TrueFalseLearningResult {
		Formula formula;
		List<Divider> dividers;
	}

	public HashMap<String, Collection<BreakpointValue>> getTrueSample() {
		return branchTrueRecord;
	}

	public HashMap<String, Collection<BreakpointValue>> getFalseSample() {
		return branchFalseRecord;
	}

	public HashMap<CfgNode, CfgNodeDomainInfo> getDominationMap() {
		return dominationMap;
	}

	@Override
	public String getLogFile() {
		return logFile;
	}

	public int nodeIdx2Offset(CfgNode node) {
		return this.relevantVars.get(node.getIdx()).getOffset();
	}

	@Override
	public void cleanup() {
		getTrueSample().clear();
		getFalseSample().clear();
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

}
