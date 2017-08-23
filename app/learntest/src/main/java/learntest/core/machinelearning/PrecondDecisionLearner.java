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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.AbstractLearningComponent;
import learntest.core.LearningMediator;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.CfgUtils;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.IlpSelectiveSampling;
import learntest.plugin.utils.Settings;
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
import variable.FieldVar;
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
	HashMap<String, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(), branchFalseRecord = new HashMap<>();
	List<VarInfo> relevantVars ;
	private String logFile ;

	public PrecondDecisionLearner(LearningMediator mediator, String logFile) {
		super(mediator);
		this.logFile = logFile;
	}

	public DecisionProbes learn(DecisionProbes inputProbes, BreakpointData bpdata, Map<Integer, List<Variable>> relevantVarMap) throws SavException {
		List<CfgNode> decisionNodes = inputProbes.getCfg().getDecisionNodes();
		DecisionProbes probes = inputProbes;
		dataPreprocessor = new LearnedDataProcessor(mediator, inputProbes);
		dominationMap = new CfgDomain().constructDominationMap(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()));
		
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("dominationMap : \n");
		for (CfgNodeDomainInfo info : dominationMap.values()) {
			if (!info.dominatees.isEmpty()) {
				sBuffer.append(info+"\n\n");
			}
		}		
		RunTimeInfo.write(logFile, sBuffer.toString());
		
		if (relevantVarMap == null || probes.getCfg().getNodeList().size() != relevantVarMap.size()) {
			log.debug("The size of CfgNodes is differnt from the size of map!!!!");
			this.relevantVars = null;
		}else {
			this.relevantVars = varsTransform(relevantVarMap, inputProbes.getOriginalVars());
		}
		
		learn(CfgUtils.getVeryFirstDecisionNode(probes.getCfg()), probes, new ArrayList<Integer>(decisionNodes.size()));
				
		return probes;
	}

	

	/**
	 * determine the responding relationship of vars between two CFG
	 * @param relevantVarMap map from linyun'ss CFG
	 * @param list nodes from LLY's CFG
	 * @return
	 */
	private List<VarInfo> varsTransform(Map<Integer, List<Variable>> relevantVarMap, List<ExecVar> originalVars) {
		HashMap<String, ExecVar> execVarMap = new HashMap<>();
		for (ExecVar execVar : originalVars) {
			execVarMap.put(execVar.getLabel(), execVar);
		}
		List<VarInfo> list = new ArrayList<>(relevantVarMap.size());
		for (Entry<Integer, List<Variable>> entry : relevantVarMap.entrySet()) {
			VarInfo info = new VarInfo(entry.getKey(), entry.getValue());
			info.setExecVar(execVarMap, originalVars);
			list.add(info);
		}
		list.sort(new VarInfoComparator());
		return list;
		
	}
	class VarInfoComparator implements Comparator<VarInfo>{

		@Override
		public int compare(VarInfo o1, VarInfo o2) {
			return o1.offset - o2.offset;
		}
		
	}
	class VarInfo {
		Integer offset;
		List<Variable> variables;
		List<ExecVar> execVars;
		VarInfo(Integer integer, List<Variable> vars){
			this.offset = integer;
			this.variables = vars;
		}
		
		public void setExecVar(HashMap<String, ExecVar> execVarMap, List<ExecVar> originalVars) {
			List<VarInfo> infos = new ArrayList<>(variables.size());
			for (Variable var : variables) {
				ExecVar execVar = execVarMap.get(var.getVarID());
				if (execVar != null) {
					VarInfo info = new VarInfo(originalVars.indexOf(execVar), null);
					info.execVars = new LinkedList<>();
					info.execVars.add(execVar);
					infos.add(info);
				}else {
					log.info(var + "does not exist in original ExecVars.");
				}
			}			
			infos.sort(new VarInfoComparator()); // keep the sequence in originalVars
			execVars = new ArrayList<>(variables.size());
			for (VarInfo varInfo : infos) {
				execVars.add(varInfo.execVars.get(0));
			}
		}
		
		//llt: backup
//		public void setExecVar(HashMap<String, ExecVar> execVarMap, List<ExecVar> originalVars) {
//			List<VarInfo> infos = new ArrayList<>(variables.size());
//			for (Variable var : variables) {
//				String label = var.getName();
//				if (var instanceof FieldVar) {
//					label = "this."+label;
//				}
//				ExecVar execVar = execVarMap.get(label);
//				if (execVar != null) {
//					VarInfo info = new VarInfo(originalVars.indexOf(execVar), null);
//					info.execVars = new LinkedList<>();
//					info.execVars.add(execVar);
//					infos.add(info);
//				}else {
//					log.info(var + "does not exist in original ExecVars.");
//				}
//			}			
//			infos.sort(new VarInfoComparator()); // keep the sequence in originalVars
//			execVars = new ArrayList<>(variables.size());
//			for (VarInfo varInfo : infos) {
//				execVars.add(varInfo.execVars.get(0));
//			}
//		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(offset + " : ");
			sb.append("Variables : "+variables+";  ");
			sb.append("ExecVars : "+execVars + "\n");
			return sb.toString();
		}
	}

	private void learn(CfgNode node, DecisionProbes probes, List<Integer> visitedNodes) throws SavException {
		if (probes.getOriginalVars().size()>50) { /** discard those method with too many variables */
			log.debug("OriginalVars size is "+probes.getOriginalVars().size() + " > 50, return directly!!!");
			return;
		}
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(node);
		while (!queue.isEmpty()) {
			node = queue.poll();
			log.debug("parsing the node in line " + node.getLine() + "(" + node + ")");

			// for (CfgNode dominator : CfgUtils.getPrecondInherentDominatee(node))
			// { /** update dominator node's condition*/
			// if (!visitedNodes.contains(dominator.getIdx())) {
			// learn(dominator, probes, visitedNodes);
			// }
			// }
			DecisionNodeProbe nodeProbe = probes.getNodeProbe(node);
			if (needToLearn(nodeProbe)) {
				List<ExecVar> targetVars ;
				if (relevantVars != null) {
					targetVars = relevantVars.get(node.getIdx()).execVars;
				}else {
					targetVars = probes.getOriginalVars();
				}
						
				Pair<OrCategoryCalculator, Boolean> pair = null;
				log.debug("learning the node in line " + node.getLine() + "(" + node + ")");
				if (node.isInLoop()) { // should be node.inLoopHeader(), todo : this is a patch, because the loop header is labeled incorrectly
					/** todo : handle the loop
					 *  loop header dominate and is dominated by nodes in loop, 
					 *  thus in order to break the wait lock, loop header should be learned first without learing nodes in loop 
					 * */
					pair = getPreconditions(probes, node, true);
				}else {
					pair = getPreconditions(probes, node, false);
				}
				
				if (!pair.second()) {
					log.debug("to learn the node in line " + node.getLine() + "(" + node + "), its dominator has to be learned before");
					queue.add(node);
					continue;
				}
				log.debug("relevant vars : "+targetVars);
				OrCategoryCalculator preconditions = pair.first();
				dataPreprocessor.sampleForBranchCvg(node, preconditions, this);
				dataPreprocessor.sampleForLoopCvg(node, preconditions, this);

				updatePrecondition(nodeProbe, preconditions, targetVars);
				nodeProbe.getPrecondition().setVisited(true);
			}else {
				nodeProbe.getPrecondition().setVisited(true);
			}

			visitedNodes.add(node.getIdx());
			List<CfgNode> childDecisonNodes = dominationMap.get(node).getDominatees();
			childDecisonNodes.sort(new DomainationComparator(dominationMap));
			for (CfgNode dependentee : CollectionUtils.nullToEmpty(childDecisonNodes)) {
				if (null != dependentee && !visitedNodes.contains(dependentee.getIdx())&& !queue.contains(dependentee)) {
					queue.add(dependentee);
				}
			}
		}
	}

	private boolean needToLearn(DecisionNodeProbe nodeProbe) {
		return !nodeProbe.areAllbranchesUncovered() || nodeProbe.needToLearnPrecond();
	}

	protected void updatePrecondition(DecisionNodeProbe nodeProbe, OrCategoryCalculator preconditions, List<ExecVar> targetVars) throws SavException {
		/* at this point only 1 branch is missing at most */
		CoveredBranches coveredType = nodeProbe.getCoveredBranches();
		TrueFalseLearningResult trueFalseResult = generateTrueFalseFormula(nodeProbe, coveredType, preconditions, targetVars);
//		Formula oneMore = generateLoopFormula(nodeProbe);
		Formula truefalseFormula = trueFalseResult == null ? null : trueFalseResult.formula;
		List<Divider> divider = trueFalseResult == null ? null : trueFalseResult.dividers;
		nodeProbe.setPrecondition(Pair.of(truefalseFormula, null), divider);
		nodeProbe.clearCache();
		
		log.info("final formula : "+ truefalseFormula);
	}

	protected Pair<OrCategoryCalculator, Boolean> getPreconditions(DecisionProbes probes, CfgNode node, boolean isLoopHeader) {
		return probes.getPrecondition(node, dominationMap, isLoopHeader);
	}

	private TrueFalseLearningResult generateTrueFalseFormula(DecisionNodeProbe orgNodeProbe,
			CoveredBranches coveredType, OrCategoryCalculator preconditions, List<ExecVar> targetVars) throws SavException {

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
		PositiveSeparationMachine mcm = new PositiveSeparationMachine(negative);		
		log.info("generate initial formula");
		trueFlaseFormula = generateInitialFormula(orgNodeProbe, mcm, targetVars);
		double acc = mcm.getModelAccuracy();
		if (mcm.getDataPoints().size()<=1) {
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
			SamplingResult sampleResult = dataPreprocessor.sampleForModel(nodeProbe, targetVars,
					preconditions, mcm.getFullLearnedDividers(mcm.getDataLabels(), orignalVars), logFile);
			if (sampleResult == null) {
				log.debug("sampling result is null");
				continue;
			}
			INodeCoveredData newData = sampleResult.getNewData(nodeProbe);
			nodeProbe.getPreconditions(preconditions).clearInvalidData(newData);
			mcm.getLearnedModels().clear();
			addDataPoint(mcm.getDataLabels(), targetVars,
					newData.getTrueValues(), newData.getFalseValues(), mcm);
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
		
		if (trueFlaseFormula != null ) {
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

	private Formula generateInitialFormula(DecisionNodeProbe nodeProbe, PositiveSeparationMachine mcm, List<ExecVar> targetVars)
			throws SAVExecutionTimeOutException {
//		DecisionProbes probes = nodeProbe.getDecisionProbes();
//		mcm.setDefaultParams();
//		List<String> labels = probes.getLabels();
//		mcm.setDataLabels(labels);
//		mcm.setDefaultParams();
//		addDataPoint(labels, probes.getOriginalVars(), targetVars,
//				nodeProbe.getTrueValues(), nodeProbe.getFalseValues(), mcm);
//		mcm.train();
//		Formula newFormula = mcm.getLearnedMultiFormula(probes.getOriginalVars(), labels);
		
		mcm.setDefaultParams();
		
		List<String> labels = new LinkedList<>();
		for (ExecVar var : targetVars) {
			labels.add(var.getLabel());
		}
		
		mcm.setDataLabels(labels);
		mcm.setDefaultParams();
		addDataPoint(mcm.getDataLabels(), targetVars,
				nodeProbe.getTrueValues(), nodeProbe.getFalseValues(), mcm);
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(targetVars, labels);

		return newFormula;
	}

	private void addDataPoint(List<String> labels, List<ExecVar> targetVars, Collection<BreakpointValue> trueV, Collection<BreakpointValue> falseV,
			PositiveSeparationMachine mcm) {
		for (BreakpointValue value : trueV) {
			addBkp(labels, targetVars, value, Category.POSITIVE, mcm);
		}
		for (BreakpointValue value : falseV) {
			addBkp(labels, targetVars, value, Category.NEGATIVE, mcm);
		}
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("add data point to svm : =============================\n");
		sBuffer.append("new data true : "+trueV.size()+" , "+trueV.toString()+"\n");
		sBuffer.append("new data false : "+falseV.size()+" , "+falseV.toString()+"\n");		
		log.info("add data point to svm :");
		log.info("new data true : "+trueV.size()+" , "+trueV.toString());
		log.info("new data false : "+falseV.size()+" , "+falseV.toString());		

		RunTimeInfo.write(logFile, sBuffer.toString());
	}

	private void addBkp(List<String> labels, List<ExecVar> targetVars, BreakpointValue bValue, 
			Category category, Machine machine) {
		double[] lineVals = new double[labels.size()];
		int i = 0;
		for (ExecVar var : targetVars) {
			final Double value ;
			value = bValue.getValue(var.getLabel(), 0.0);
			lineVals[i++] = value;
		}
		/** below is about derived vars like x*y */
//		if (i < labels.size()) {
//			int size = targetVars.size();
//			for (int j = 0; j < size; j++) {
//				 double value = bValue.getValue(targetVars.get(j).getLabel(), 0.0);
//				for (int k = j + 1; k < size; k++) {
//					 lineVals[i ++] = value * bValue.getValue(targetVars.get(k).getLabel(), 0.0);
//				}
//			}
//		}

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

	@Override
	public String getLogFile() {
		return logFile;
	}
	
	
}
