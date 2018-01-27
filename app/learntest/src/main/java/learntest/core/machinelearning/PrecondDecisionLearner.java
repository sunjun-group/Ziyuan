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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearningMediator;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.decision.Precondition;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.VariableUtils;
import learntest.core.commons.utils.VariableUtils.VarInfo;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.IlpSelectiveSampling;
import learntest.core.rule.EqualVarRelationShip;
import learntest.core.rule.NotEqualVarRelationShip;
import learntest.core.rule.RelationShip;
import learntest.core.time.CovTimer;
import learntest.plugin.utils.Settings;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.formula.OrFormula;
import sav.common.core.utils.FileUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.execute.value.ExecVar;
import variable.Variable;

/**
 * @author LLT
 *
 */
public class PrecondDecisionLearner extends AbstractDecisionLearner {
	private static Logger log = LoggerFactory.getLogger(PrecondDecisionLearner.class);
	private static int FORMULAR_LEARN_MAX_ATTEMPT = 10;
	protected LearnedDataProcessor dataPreprocessor;
	public HashMap<CfgNode, FormulaInfo> learnedFormulas = new HashMap<>();

	HashMap<String, Collection<BreakpointValue>> branchTrueRecord = new HashMap<>(),
			branchFalseRecord = new HashMap<>();
	List<VarInfo> relevantVars;
	private String logFile;

	private CompilationUnit cu;
	private int symoblicTime = 0;
	private String initialTc;
	
	public PrecondDecisionLearner(LearningMediator mediator, String logFile) {
		super(mediator);
		this.logFile = logFile;
		RunTimeInfo.createFile(logFile);
	}

	@Override
	protected void prepareDataBeforeLearn(DecisionProbes inputProbes, Map<Integer, List<Variable>> relevantVarMap)
			throws SavException {
		dataPreprocessor = new LearnedDataProcessor(mediator, inputProbes);
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("dominationMap : \n");
		for (CfgNodeDomainInfo info : dominationMap.values()) {
			if (!info.dominatees.isEmpty()) {
				sBuffer.append(info + "\n\n");
			}
		}
		FileUtils.write(logFile, sBuffer.toString());
		log.info(sBuffer.toString());
		
		if (relevantVarMap == null || inputProbes.getCfg().getNodeList().size() != relevantVarMap.size()) {
			log.debug("The size of CfgNodes is differnt from the size of map!!!!");
			this.relevantVars = null;
		} else {
			this.relevantVars = VariableUtils.varsTransform(relevantVarMap, inputProbes.getOriginalVars());
		}
	}
	
	protected CfgNode learn(DecisionNodeProbe nodeProbe, List<Integer> visitedNodes, int loopTimes)
			throws SavException {
		DecisionProbes probes = nodeProbe.getDecisionProbes();
		CfgNode node = nodeProbe.getNode();
		if (needToLearn(nodeProbe)) {
			List<ExecVar> targetVars;
//			relevantVars = null;
			if (relevantVars != null) {
				targetVars = relevantVars.get(node.getIdx()).getExecVars();
			} else {
				targetVars = probes.getOriginalVars();
			}

			Pair<OrCategoryCalculator, Boolean> pair = null;
			log.debug("learning the node in line " + node.getLine() + "(" + node + ")");
			if (loopTimes < 70 ? node.isLoopHeader() : node.isInLoop()) { // give a simple patch when there is a bug that will cause infinite loop
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
				return node;
			}
			log.debug("relevant vars : " + targetVars);
			OrCategoryCalculator preconditions = pair.first();
			// dataPreprocessor.sampleForBranchCvg(node, preconditions, this);
			// dataPreprocessor.sampleForLoopCvg(node, preconditions, this);
			RelationShip relationShip = null;
//			if (indexMap.containsKey(node.getLine())) {
//				int index = indexMap.get(node.getLine()).indexOf(node);
//				Visitor visitor = new Visitor(node.getLine(), cu, index);
//				cu.accept(visitor);
//				relationShip = visitor.getRelationShip();
//			}
			if (relationShip != null) { // heuristic rules help to get condition directly
				updatePreconditionWithHerustic(nodeProbe, preconditions, relationShip);
				
			} else {
				dataPreprocessor.sampleForBranchCvg(node, preconditions, this);
				boolean ifInvokeSolver = dataPreprocessor.sampleForMissingBranch(node, this, initialTc);
				if (ifInvokeSolver) {
					symoblicTime++;
				}
				log.debug("vars.size = {}", targetVars.size());
				if (targetVars.size() > 50) { 
					log.debug("targetVars size is " + targetVars.size() + " > 50, skip learn!!!");
				}else {
					updatePrecondition(nodeProbe, preconditions, targetVars);
				}
			}
			nodeProbe.getPrecondition().setVisited(true);
		} else {
			nodeProbe.getPrecondition().setVisited(true);
			log.debug("no need to learn the node in line " + node.getLine() + "(" + node + ")");
		}
		return null;
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
	
	protected void updatePreconditionWithHerustic(DecisionNodeProbe nodeProbe,	OrCategoryCalculator preconditions, RelationShip relationShip) throws SavException {
		TrueFalseLearningResult trueFalseResult = generateTrueFalseFormulaByHerustic(nodeProbe, preconditions, relationShip);
		Formula truefalseFormula = trueFalseResult == null ? null : trueFalseResult.formula;
		if (truefalseFormula != null) {
			CfgNode node = nodeProbe.getNode();
			learnedFormulas.put(node, new FormulaInfo(node));
			learnedFormulas.get(node).addTFFormula(truefalseFormula.toString(), 1.1);
			nodeProbe.getPrecondition().setType(trueFalseResult.type);
		}
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
			if (CovTimer.stopFlag) {
				break;
			}
			/** record learned formulas */
			if (!learnedFormulas.containsKey(node)) {
				learnedFormulas.put(node, new FormulaInfo(node));
			}
			learnedFormulas.get(node).addTFFormula(trueFlaseFormula.toString(), acc);

			IlpSelectiveSampling.iterationTime = FORMULAR_LEARN_MAX_ATTEMPT - time;
			time++;
			DecisionProbes probes = nodeProbe.getDecisionProbes();

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

			System.out.println("the whole data points for this learning:");
			if(orgNodeProbe.getCoverage().getCfgNode().getLine()==2345){
				List<DataPoint> ps = new ArrayList<>();
				for(DataPoint p: mcm.getDataPoints()){
					String str = p.toString();
					String s = str.replace("[", "(");
					s = s.replace("]", ",)");
					if(str.contains("POS")){
						ps.add(p);
					}
					System.out.println(s);
				}
				
				if(ps.size()>1){
					System.currentTimeMillis();
				}
			}			
			
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
		System.currentTimeMillis();
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
//		addDataPointForTest2(mcm);
		mcm.train();
		Formula newFormula = mcm.getLearnedMultiFormula(targetVars, labels);

		return newFormula;
	}

	private TrueFalseLearningResult generateTrueFalseFormulaByHerustic(DecisionNodeProbe nodeProbe, OrCategoryCalculator preconditions, RelationShip relationShip) throws SavException {
		List<ExecVar> originalVars = nodeProbe.getDecisionProbes().getOriginalVars();
		List<String> labels = new LinkedList<>();
		for (ExecVar var : originalVars) {
			labels.add(var.getLabel());
		}
		List<Divider> dividers = null;
		System.currentTimeMillis();
		Formula trueFlaseFormula = null;
		int type = 0;
		
		if (relationShip instanceof EqualVarRelationShip) {
			type = Precondition.ISEQUAL;
			dividers = constructDividers(labels, relationShip, 0);
			for (Divider divider : dividers) {
				Formula current = new FormulaProcessor<ExecVar>(originalVars).process(divider, labels, true);
				if (trueFlaseFormula == null) {
					trueFlaseFormula = current;
				} else {
					trueFlaseFormula = new AndFormula(trueFlaseFormula, current);
				}
			}
			dataPreprocessor.sampleForModel(nodeProbe, originalVars, preconditions, dividers, logFile);
		}else if (relationShip instanceof NotEqualVarRelationShip) {
			type = Precondition.ISNOTEQUAL;
			dividers = constructDividers(labels, relationShip, 0.1);

			for (Divider divider : dividers) {
				Formula current = new FormulaProcessor<ExecVar>(originalVars).process(divider, labels, true);
				if (trueFlaseFormula == null) {
					trueFlaseFormula = current;
				} else {
					trueFlaseFormula = new OrFormula(trueFlaseFormula, current);
				}
			}
			for (Divider divider : dividers) {
				List<Divider> tDividers = new LinkedList<>();
				int theta0 = (int)((Math.random()>0.5?1:-1) * Math.random() * Settings.getBound());/* sample will take border of divider, thus prefer a random int rather than 0*/
				Divider tDivider = new Divider(divider.getThetas(), theta0); 
				tDividers.add(tDivider);
				dataPreprocessor.sampleForModel(nodeProbe, originalVars, preconditions, tDividers, logFile);
			}
		}else {
			dataPreprocessor.sampleForModel(nodeProbe, originalVars, preconditions, dividers, logFile);
		}

		TrueFalseLearningResult result = new TrueFalseLearningResult();
		result.formula = trueFlaseFormula;
		result.dividers = dividers;
		result.type = type;
		return result;
	}

	private List<Divider> constructDividers(List<String> labels, RelationShip relationShip, double theta0) {
		List<Divider> dividers = new LinkedList<>();
		if (relationShip instanceof EqualVarRelationShip || relationShip instanceof NotEqualVarRelationShip) {
			double[] result1 = new double[labels.size()], result2 = new double[labels.size()];
			String left = relationShip.getLeft(), right = relationShip.getRight();
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
			
			Divider divider1 = new Divider(result1, theta0), divider2 = new Divider(result2, theta0);
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

		FileUtils.write(logFile, sBuffer.toString());
	}
	
	private void addDataPointForTest(PositiveSeparationMachine mcm) {
		List<double[]> trueV = new LinkedList<>(), falseV = new LinkedList<>();
		for (int i = 0; i < 10; i++) {
			double a = Math.random() * 100;
			double b = Math.random() * 100;
			double[] t = new double[2];
			t[0] = a+b;
			t[1] = a;
			trueV.add(t);
		}
		for (int i = 0; i < 10; i++) {
			double a = Math.random() * 100;
			double b = Math.random() * 100;
			double[] f = new double[2];
			f[0] = a-b;
			f[1] = a;
			falseV.add(f);
		}
		for (double[] doubles : trueV) {
			mcm.addDataPoint(Category.POSITIVE, doubles);
		}
		for (double[] ds : falseV) {
			mcm.addDataPoint(Category.NEGATIVE, ds);
		}
	}
	
	private void addDataPointForTest2(PositiveSeparationMachine mcm) {
		List<double[]> trueV = new LinkedList<>(), falseV = new LinkedList<>();
		trueV.add(new double[]{11.41168212890625, -0.009999999776482582});
		trueV.add(new double[]{1000.0, -0.009999999776482582}); 
		 trueV.add(new double[]{417.6746520996094, -535.624267578125}); 
		 trueV.add(new double[]{235.05567932128906, 225.53347778320312}); 
		 trueV.add(new double[]{362.89715576171875, -875.3241577148438}); 
		 trueV.add(new double[]{989.5972290039062, -169.92794799804688}); 
		 trueV.add(new double[]{445.5552062988281, -774.032958984375}); 
		 trueV.add(new double[]{551.253173828125, -274.4393310546875}); 
		 trueV.add(new double[]{494.5365295410156, -194.18775939941406}); 
		 trueV.add(new double[]{706.8424072265625, -193.26675415039062}); 
		 trueV.add(new double[]{992.8089599609375, -581.1283569335938}); 
		 trueV.add(new double[]{440.0, 226.0}); 
		 trueV.add(new double[]{340.0, -561.0}); 
		 trueV.add(new double[]{499.0, -101.0}); 
		 trueV.add(new double[]{887.0, -667.0}); 
		 trueV.add(new double[]{331.0, -76.0}); 
		 trueV.add(new double[]{120.0, -856.0}); 
		 trueV.add(new double[]{439.0, -685.0}); 
		 trueV.add(new double[]{122.0, -133.0}); 
		 trueV.add(new double[]{363.0, -765.0}); 
		 trueV.add(new double[]{173.0, 19.0}); 
		 trueV.add(new double[]{465.0, 410.0}); 
		 trueV.add(new double[]{776.0, 693.0}); 
		 trueV.add(new double[]{929.0, 795.0}); 
		 trueV.add(new double[]{954.0, 182.0}); 
		 trueV.add(new double[]{522.0, -973.0}); 
		 trueV.add(new double[]{242.0, -738.0}); 
		 trueV.add(new double[]{714.0, -302.0}); 
		 trueV.add(new double[]{993.0, 151.0}); 
		 trueV.add(new double[]{599.0, 358.0}); 
		 trueV.add(new double[]{247.0, -108.0}); 
		 trueV.add(new double[]{567.0, -51.0}); 
		 trueV.add(new double[]{151.0, -818.0}); 
		 trueV.add(new double[]{366.0, -50.0}); 
		 trueV.add(new double[]{287.0, 200.0}); 
		 trueV.add(new double[]{529.0, 220.0}); 
		 trueV.add(new double[]{853.0, -270.0}); 
		 trueV.add(new double[]{564.0, -584.0}); 
		 falseV.add(new double[]{-1000.0,1000.0});
		 falseV.add(new double[]{4.975438,412.98987});
		 falseV.add(new double[]{-747.6848,-554.0581});
		 falseV.add(new double[]{-916.436,955.3757});
		 falseV.add(new double[]{-307.281,828.59863});
		 falseV.add(new double[]{-817.5938,723.9331});
		 falseV.add(new double[]{-925.1779,897.93634});
		 falseV.add(new double[]{-610.1471,968.7815});
		 falseV.add(new double[]{-800.6481,937.58716});
		 falseV.add(new double[]{-685.79004,744.10095});
		 falseV.add(new double[]{-982.26514,-353.3182});
		 falseV.add(new double[]{-756.4198,-583.47894});
		 falseV.add(new double[]{-767.6957,-140.8242});
		 falseV.add(new double[]{-34.200073,-547.35675});
		 falseV.add(new double[]{-180.80199,-486.65643});
		 falseV.add(new double[]{-130.9638,-855.9438});
		 falseV.add(new double[]{-275.5102,-177.67763});
		 falseV.add(new double[]{-341.00723,-307.17944});
		 falseV.add(new double[]{-790.8075,-443.3944});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-965.0,96.0});
		 falseV.add(new double[]{-230.0,23.0});
		 falseV.add(new double[]{-864.0,952.0});
		 falseV.add(new double[]{-31.0,554.0});
		 falseV.add(new double[]{-1000.0,-720.517});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-1000.0,-720.517});
		 falseV.add(new double[]{-522.0854,-1000.0});
		 falseV.add(new double[]{-1000.0,-720.517});
		 falseV.add(new double[]{-523.9754,-1001.89});
		 falseV.add(new double[]{-523.9754,-1001.89});
		 falseV.add(new double[]{-523.9754,-1001.89});
		 falseV.add(new double[]{-998.11,-718.627});
		 falseV.add(new double[]{-998.11,-718.627});
		 falseV.add(new double[]{-284.0,-719.0});
		 falseV.add(new double[]{-253.0,-212.0});
		 falseV.add(new double[]{-972.0,-491.0});
		 falseV.add(new double[]{-399.0,-177.0});
		 falseV.add(new double[]{-97.0,-647.0});
		 falseV.add(new double[]{-692.0,971.0});
		 falseV.add(new double[]{-940.0,874.0});
		 falseV.add(new double[]{122.0,1000.0});
		 falseV.add(new double[]{561.0,590.0});
		 falseV.add(new double[]{-379.0,399.0});
		 falseV.add(new double[]{58.0,544.0});
		 falseV.add(new double[]{-358.0,601.0});
		 falseV.add(new double[]{-763.5869,-1000.0});
		 falseV.add(new double[]{-763.5869,-1000.0});
		 falseV.add(new double[]{-763.5869,-1000.0});
		 falseV.add(new double[]{-1000.0,-860.25836});
		 falseV.add(new double[]{-764.7324,-1001.1455});
		 falseV.add(new double[]{-764.7324,-1001.1455});
		 falseV.add(new double[]{-762.4414,-998.8545});
		 falseV.add(new double[]{-998.8545,-859.11285});
		 falseV.add(new double[]{-1001.1455,-861.4039});
		 falseV.add(new double[]{-996.97955,-857.2379});
		 falseV.add(new double[]{-760.56647,-996.97955});
		 falseV.add(new double[]{-766.60736,-1003.02045});
		 falseV.add(new double[]{-996.97955,-857.2379});
		 falseV.add(new double[]{-760.56647,-996.97955});
		 falseV.add(new double[]{-232.0,-759.0});
		 falseV.add(new double[]{-959.0,-175.0});
		 falseV.add(new double[]{-93.0,-496.0});
		 falseV.add(new double[]{-676.0,-789.0});
		 falseV.add(new double[]{-880.0,955.0});
		 falseV.add(new double[]{-763.5869,-1000.0});
		 falseV.add(new double[]{-134.0,87.0});
		 falseV.add(new double[]{-697.0,694.0});
		 falseV.add(new double[]{-62.0,708.0});
		 falseV.add(new double[]{-605.0,48.0});
		 falseV.add(new double[]{-885.8043,-1000.0});
		 falseV.add(new double[]{-1000.0,-931.04767});
		 falseV.add(new double[]{-885.8043,-1000.0});
		 falseV.add(new double[]{-885.8043,-1000.0});
		 falseV.add(new double[]{-882.7564,-996.9521});
		 falseV.add(new double[]{-888.85223,-1003.0479});
		 falseV.add(new double[]{-996.9521,-927.99976});
		 falseV.add(new double[]{-882.7564,-996.9521});
		 falseV.add(new double[]{-888.85223,-1003.0479});
		 falseV.add(new double[]{-996.9521,-927.99976});
		 falseV.add(new double[]{-882.7564,-996.9521});
		 falseV.add(new double[]{-1002.17413,-933.2218});
		 falseV.add(new double[]{-887.97845,-1002.17413});
		 falseV.add(new double[]{-883.6302,-997.82587});
		 falseV.add(new double[]{-429.0,-104.0});
		 falseV.add(new double[]{-324.0,-851.0});
		 falseV.add(new double[]{-55.0,-986.0});
		 falseV.add(new double[]{-885.8043,-1000.0});
		 falseV.add(new double[]{-441.0,271.0});
		 falseV.add(new double[]{745.0,857.0});
		 falseV.add(new double[]{-9.0,879.0});
		 falseV.add(new double[]{-941.0,174.0});
		 falseV.add(new double[]{-885.0,928.0});
		 falseV.add(new double[]{-378.0,802.0});
		 falseV.add(new double[]{-1000.0,-967.28107});
		 falseV.add(new double[]{-946.90027,-1000.0});
		 falseV.add(new double[]{-946.90027,-1000.0});
		 falseV.add(new double[]{-946.90027,-1000.0});
		 falseV.add(new double[]{-1005.23334,-972.5144});
		 falseV.add(new double[]{-1005.23334,-972.5144});
		 falseV.add(new double[]{-994.76666,-962.0477});
		 falseV.add(new double[]{-950.2712,-1003.3709});
		 falseV.add(new double[]{-1003.3709,-970.652});
		 falseV.add(new double[]{-996.6291,-963.91016});
		 falseV.add(new double[]{-950.2712,-1003.3709});
		 falseV.add(new double[]{-943.52936,-996.6291});
		 falseV.add(new double[]{-950.2712,-1003.3709});
		 falseV.add(new double[]{-996.6291,-963.91016});
		 falseV.add(new double[]{-986.0,-265.0});
		 falseV.add(new double[]{-795.0,-8.0});
		 falseV.add(new double[]{-788.0,-198.0});
		 falseV.add(new double[]{-1000.0,-967.28107});
		 falseV.add(new double[]{-949.0,987.0});
		 falseV.add(new double[]{-286.0,246.0});
		 falseV.add(new double[]{496.0,894.0});
		 falseV.add(new double[]{-404.0,615.0});
		 falseV.add(new double[]{334.0,764.0});
		 falseV.add(new double[]{17.0,73.0});
		 falseV.add(new double[]{-978.1402,-1000.0});
		 falseV.add(new double[]{-978.1402,-1000.0});
		 falseV.add(new double[]{-978.1402,-1000.0});
		 falseV.add(new double[]{-1000.0,-987.7258});
		 falseV.add(new double[]{-983.4117,-1005.2715});
		 falseV.add(new double[]{-983.4117,-1005.2715});
		 falseV.add(new double[]{-983.4117,-1005.2715});
		 falseV.add(new double[]{-994.7285,-982.45435});
		 falseV.add(new double[]{-1005.2715,-992.9973});
		 falseV.add(new double[]{-983.4117,-1005.2715});
		 falseV.add(new double[]{-982.6199,-1004.4797});
		 falseV.add(new double[]{-982.6199,-1004.4797});
		 falseV.add(new double[]{-1004.4797,-992.2055});
		 falseV.add(new double[]{-1004.4797,-992.2055});
		 falseV.add(new double[]{-234.0,-584.0});
		 falseV.add(new double[]{-534.0,-318.0});
		 falseV.add(new double[]{-232.0,-279.0});
		 falseV.add(new double[]{-224.0,-905.0});
		 falseV.add(new double[]{-235.0,-509.0});
		 falseV.add(new double[]{-978.1402,-1000.0});
		 falseV.add(new double[]{-999.5003,-998.6381});
		 falseV.add(new double[]{-997.4088,-999.579});
		 falseV.add(new double[]{-999.2556,-999.6198});
		 falseV.add(new double[]{-998.22955,-999.6265});
		 falseV.add(new double[]{-999.6759,-999.3532});
		 falseV.add(new double[]{-997.16406,-999.9733});
		 falseV.add(new double[]{-996.9872,-999.98773});
		 falseV.add(new double[]{-999.82825,-999.8388});
		 falseV.add(new double[]{-998.5412,-999.77167});
		 falseV.add(new double[]{-998.5827,-999.47955});
		 falseV.add(new double[]{-998.8243,-998.806});
		 falseV.add(new double[]{-999.72186,-998.462});
		 falseV.add(new double[]{-999.5528,-999.2741});
		 falseV.add(new double[]{-999.8518,-998.5692});
		 falseV.add(new double[]{-996.69794,-999.85376});
		 falseV.add(new double[]{-998.9673,-999.7864});
		 falseV.add(new double[]{-997.6012,-999.6267});
		 falseV.add(new double[]{-999.04504,-999.1264});
		 falseV.add(new double[]{-999.9565,-998.45074});
		 falseV.add(new double[]{-998.8802,-998.96484});
		 falseV.add(new double[]{-997.89624,-999.57745});
		 falseV.add(new double[]{-999.81024,-999.53906});
		 falseV.add(new double[]{-996.7866,-999.8098});
		 falseV.add(new double[]{-999.76886,-998.32544});
		 falseV.add(new double[]{-999.2246,-999.5061});
		 falseV.add(new double[]{-998.8862,-999.6853});
		 falseV.add(new double[]{-999.8659,-999.6272});
		 falseV.add(new double[]{-999.7632,-999.00574});
		 falseV.add(new double[]{-998.7911,-999.8713});
		 falseV.add(new double[]{-996.40405,-1000.0});
		 falseV.add(new double[]{-999.4999,-998.8637});
		 falseV.add(new double[]{-998.4333,-999.93304});
		 falseV.add(new double[]{-998.464,-998.9353});
		 falseV.add(new double[]{-999.34973,-998.5458});
		 falseV.add(new double[]{-999.13617,-999.3826});
		 falseV.add(new double[]{-999.5743,-999.0835});
		 falseV.add(new double[]{-999.8347,-998.18506});
		 falseV.add(new double[]{-998.06586,-999.57904});
		 falseV.add(new double[]{-999.9674,-999.5739});
		 falseV.add(new double[]{-999.23834,-999.5472});
		 falseV.add(new double[]{-999.1438,-999.5118});
		 falseV.add(new double[]{-997.3972,-999.99146});
		 falseV.add(new double[]{-999.4637,-999.75214});
		 falseV.add(new double[]{-999.918,-999.5622});
		 falseV.add(new double[]{-999.99304,-998.88074});
		 falseV.add(new double[]{-999.59894,-998.3527});
		 falseV.add(new double[]{-998.62897,-999.4692});
		 falseV.add(new double[]{-998.46875,-998.8905});
		 falseV.add(new double[]{-997.8518,-999.49335});
		 falseV.add(new double[]{-997.7784,-999.6709});
		 falseV.add(new double[]{-998.12524,-999.15845});
		 falseV.add(new double[]{-999.88257,-998.92505});
		 falseV.add(new double[]{-997.56683,-999.3583});
		 falseV.add(new double[]{-999.9081,-999.4274});
		 falseV.add(new double[]{-997.8408,-999.8036});
		 falseV.add(new double[]{-998.20667,-999.37024});
		 falseV.add(new double[]{-998.92975,-999.8721});
		 falseV.add(new double[]{-999.35443,-999.1948});
		 falseV.add(new double[]{-998.2707,-999.4698});
		for (double[] doubles : trueV) {
			mcm.addDataPoint(Category.POSITIVE, doubles);
		}
		for (double[] ds : falseV) {
			mcm.addDataPoint(Category.NEGATIVE, ds);
		}
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

	private static class TrueFalseLearningResult {
		Formula formula;
		List<Divider> dividers;
		int type;
	}
	
	@Override
	public String getLogFile() {
		return logFile;
	}
	
	public HashMap<String, Collection<BreakpointValue>> getTrueSample() {
		return branchTrueRecord;
	}

	public HashMap<String, Collection<BreakpointValue>> getFalseSample() {
		return branchFalseRecord;
	}
	
	public int nodeIdx2Offset(CfgNode node) {
		return this.relevantVars.get(node.getIdx()).getOffset();
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public int getSymoblicTimes() {
		return symoblicTime;
	}

	public String getInitialTc() {
		return initialTc;
	}

	public void setInitialTc(String initialTc) {
		this.initialTc = initialTc;
	}
}
