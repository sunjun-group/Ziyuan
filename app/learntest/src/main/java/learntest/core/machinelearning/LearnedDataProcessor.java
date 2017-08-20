/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.BranchType;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.decision.IDecisionNode;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import sav.common.core.SavException;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT 
 * after running selective sampling, decision coverages will be
 *  updated based on running new testcases which are generated based on
 *  new sample data.
 */
public class LearnedDataProcessor {
	private static Logger log = LoggerFactory.getLogger(LearnedDataProcessor.class);
	private SelectiveSampling<SamplingResult> selectiveSampling;
	private DecisionProbes decisionProbes;
	
	public LearnedDataProcessor(LearningMediator mediator, DecisionProbes decisionProbes) {
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, decisionProbes);
		this.selectiveSampling = new SelectiveSampling<SamplingResult>(sampleExecutor, decisionProbes);
		this.decisionProbes = decisionProbes;
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
//			List<Divider> dividers = new ArrayList<>();
//			if(preconditions!=null && !preconditions.getCalculators().isEmpty()){
//				List<CategoryCalculator> list = preconditions.getCalculators().get(0);
//				if(!list.isEmpty()){
//					CategoryCalculator cal = list.get(0);
//					if(cal instanceof MultiDividerBasedCategoryCalculator){
//						MultiDividerBasedCategoryCalculator mCalculator = (MultiDividerBasedCategoryCalculator)cal;
//						dividers = mCalculator.getDividers();
//					}
//				}
//			}
			
			selectDataForEmpty(nodeProbe, 
					preconditions, null, coveredType.getOnlyOneMissingBranch(), false, learner);
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
			learner.recordSample(decisionProbes, selectResult);
			
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
	 * @param learner 
	 */
	public void sampleForLoopCvg(CfgNode node,
			OrCategoryCalculator preconditions, IInputLearner learner) throws SavException {
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
			OrCategoryCalculator preconditions, List<Divider> learnedDividers)
			throws SavException {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("select sample with precondition : ");
		for (List<CategoryCalculator> list : preconditions.getCalculators()) {
			sBuffer.append("(");
			for (CategoryCalculator cc : list) {
				sBuffer.append(cc);
				sBuffer.append("&&");
			}
			sBuffer.append(")||");
		}
		sBuffer.append("\nselect sample with learnedDividers : ");
		if (learnedDividers != null) {
			for (Divider d : learnedDividers) {
				sBuffer.append(d+",");
			}
		}
		log.info(sBuffer.toString());
		return selectiveSampling.selectDataForModel(nodeProbe, originalVars, preconditions, learnedDividers);
	}
	
	public SamplingResult getSampleOfInitCase(BreakpointData result, List<ExecVar> vars) throws SavException {
		List<double[]> list = new LinkedList<>();
		for (BreakpointValue bpv : result.getAllValues()) {
			double[] values = bpv.getAllValues();
			list.add(values);
		}
		return selectiveSampling.runData(list, vars);
		
	}
}
