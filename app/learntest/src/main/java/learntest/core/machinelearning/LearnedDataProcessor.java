/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfg.CfgNode;
import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import jdart.model.TestInput;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.BranchType;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.IDecisionNode;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.VarSolutionUtils;
import learntest.core.gentest.GentestResult;
import learntest.core.jdart.JDartRunner;
import learntest.core.jdart.JdartTestInputUtils;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import sav.common.core.SavException;
import sav.common.core.utils.FileUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT after running selective sampling, decision coverages will be
 *         updated based on running new testcases which are generated based on
 *         new sample data.
 */
public class LearnedDataProcessor {
	private static Logger log = LoggerFactory.getLogger(LearnedDataProcessor.class);
	private SelectiveSampling<SamplingResult> selectiveSampling;
	private DecisionProbes decisionProbes;
	private LearningMediator mediator;

	public LearnedDataProcessor(LearningMediator mediator, DecisionProbes decisionProbes) {
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, decisionProbes);
		this.selectiveSampling = new SelectiveSampling<SamplingResult>(sampleExecutor, decisionProbes);
		this.decisionProbes = decisionProbes;
		this.mediator = mediator;
	}

	public DecisionProbes sampleForBranchCvg(CfgNode node, OrCategoryCalculator preconditions, IInputLearner learner)
			throws SavException {
		DecisionNodeProbe nodeProbe = decisionProbes.getNodeProbe(node);
		/*
		 * if all branches are missing, nothing we can do, and if all branches
		 * are covered, then do not need to do anything
		 */
		if (nodeProbe.areAllbranchesUncovered()) {
			return decisionProbes;
		}

		CoveredBranches coveredType;
		DecisionProbes processedProbes = decisionProbes;
		coveredType = nodeProbe.getCoveredBranches();
		if (coveredType.isOneBranchMissing()) {
			selectDataForEmpty(nodeProbe, preconditions, null, coveredType.getOnlyOneMissingBranch(), false, learner);
		}

		return processedProbes;
	}

	public SamplingResult selectDataForEmpty(IDecisionNode nodeProbe, OrCategoryCalculator precondition,
			List<Divider> divider, BranchType missingBranch, boolean isLoop, IInputLearner learner)
			throws SavException, SAVExecutionTimeOutException {
		/* try to select 2 times */
		SamplingResult selectResult = null;
		for (int i = 0; i < 2; i++) {
			selectResult = selectiveSampling.selectData(decisionProbes.getOriginalVars(), precondition, divider);
			if (selectResult == null) {
				continue;
			}
			learner.recordSample(decisionProbes, selectResult, learner.getLogFile());

			INodeCoveredData selectData = selectResult.getNewData(nodeProbe);
			if (!isLoop) {
				if ((missingBranch.isTrueBranch()) && !selectData.getTrueValues().isEmpty()) {
					return selectResult;
				}
				if (missingBranch.isFalseBranch() && !selectData.getFalseValues().isEmpty()) {
					return selectResult;
				}
			} else {
				if (missingBranch.isTrueBranch() && !selectData.getMoreTimesValues().isEmpty()) {
					return selectResult;
				}
				if (missingBranch.isFalseBranch() && !selectData.getOneTimeValues().isEmpty()) {
					return selectResult;
				}
			}
		}
		return selectResult;
	}

	/**
	 * only run sampling if node is loop header and its true branch is covered.
	 * 
	 * @param learner
	 */
	public void sampleForLoopCvg(CfgNode node, OrCategoryCalculator preconditions, IInputLearner learner)
			throws SavException {
		DecisionNodeProbe nodeProbe = decisionProbes.getNodeProbe(node);
		if (!node.isLoopHeader() || !nodeProbe.getCoveredBranches().coversTrue()
				|| (!nodeProbe.getOneTimeValues().isEmpty() && !nodeProbe.getMoreTimesValues().isEmpty())) {
			return;
		}
		BranchType missingBranch = nodeProbe.getMoreTimesValues().isEmpty() ? BranchType.TRUE
				: BranchType.FALSE; /* ?? */
		selectDataForEmpty(nodeProbe, preconditions, null, missingBranch, true, learner);
	}

	public SamplingResult sampleForModel(DecisionNodeProbe nodeProbe, List<ExecVar> originalVars,
			OrCategoryCalculator preconditions, List<Divider> learnedDividers, String logFile, boolean seperateDividers) throws SavException {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("vars : " + originalVars + "\n");
		sBuffer.append("select sample with precondition : ");
		for (List<CategoryCalculator> list : preconditions.getCalculators()) {
			sBuffer.append("(");
			for (CategoryCalculator cc : list) {
				sBuffer.append(cc);
				sBuffer.append("&&");
			}
			sBuffer.append(")||");
		}
		sBuffer.append("\nselect sample with learnedDividers (" + seperateDividers + ") : ");
		if (learnedDividers != null) {
			for (Divider d : learnedDividers) {
				sBuffer.append(d + ",");
			}
		}
		log.info(sBuffer.toString());
		sBuffer.append("\n");
		FileUtils.write(logFile, sBuffer.toString());
		return selectiveSampling.selectDataForModel(nodeProbe, originalVars, preconditions, learnedDividers,seperateDividers);
	}

	/**
	 * 
	 * @param node
	 * @param precondDecisionLearner
	 * @param initialTc 
	 * @return  if invoke symbolic solver
	 */
	public boolean sampleForMissingBranch(CfgNode node, PrecondDecisionLearner precondDecisionLearner, String initialTc) {
		DecisionNodeProbe nodeProbe = decisionProbes.getNodeProbe(node);
		/*
		 * if all branches are missing, nothing we can do, and if all branches
		 * are covered, then do not need to do anything
		 */
		if (nodeProbe.areAllbranchesUncovered()) {
			return false;
		}

		CoveredBranches coveredType = nodeProbe.getCoveredBranches();
		if (coveredType.isOneBranchMissing()) {
			BranchType missingBranch = coveredType.getOnlyOneMissingBranch();
			/**
			 * we may only get one data point as the neighbor.
			 */
			List<ExecVar> vars = decisionProbes.getOriginalVars();
			List<double[]> solution = getNeighborTc(missingBranch, nodeProbe, vars);
			try {
				GentestResult mainResult = mediator.genMainAndCompile(solution, vars, PrintOption.APPEND);
				List<File> generatedClasses = mainResult.getAllFiles();
				File generatedClasse = generatedClasses.get(0);
				String generatedClassName = mainResult.getJunitClassNames().get(0);
				log.info("generated class names : " + generatedClasse.getAbsolutePath());
				JDartRunner jdartRunner = new JDartRunner(mediator.getAppClassPath());
				List<TestInput> result = jdartRunner.runJDartOnDemand(mediator.getLearntestParams(), generatedClassName, initialTc,
						node.getIdx(), 	missingBranch == BranchType.FALSE ? 0 : 1);
								
				if (result != null && result.size() > 0) {
					List<BreakpointValue> bkpVals = JdartTestInputUtils.toBreakpointValue(result,
							mediator.getLearntestParams().getTargetMethod().getMethodFullName());
					List<ExecVar> bkpVars = BreakpointDataUtils.collectAllVarsInturn(bkpVals); //todo : bkpVars may be different from vars 
					List<double[]> solutions = VarSolutionUtils.buildSolutions(bkpVals, bkpVars);
					selectiveSampling.runData(solutions, bkpVars);
					System.currentTimeMillis();
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	private List<double[]> getNeighborTc(BranchType missingBranch, DecisionNodeProbe nodeProbe, List<ExecVar> vars) {
		List<double[]> list = new LinkedList<>();
		List<BreakpointValue> dataPoints =  missingBranch == BranchType.FALSE ? nodeProbe.getTrueValues() : nodeProbe.getFalseValues();
		BreakpointValue dataPoint = dataPoints.get(0);
		double[] values = new double[vars.size()];
		int i =0;
		for (ExecVar var : vars) {
			values[i] = dataPoint.getValue(var.getVarId(), (double)0);
			i++;
		}
		list.add(dataPoint.getAllValues());
		return list;
	}

}
