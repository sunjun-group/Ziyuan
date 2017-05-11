/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.CoveredBranches;
import learntest.core.commons.data.decision.DecisionNodeProbe;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.testcase.data.BranchType;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import sav.common.core.SavException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT 
 * after running selective sampling, decision coverages will be
 *  updated based on running new testcases which are generated based on
 *  new sample data.
 */
public class LearnedDataProcessor {
	private JavailpSelectiveSampling<SamplingResult> selectiveSampling;
	private DecisionProbes decisionProbes;
	
	public LearnedDataProcessor(LearningMediator mediator, DecisionProbes decisionProbes) {
		SampleExecutor sampleExecutor = new SampleExecutor(mediator, decisionProbes);
		this.selectiveSampling = new JavailpSelectiveSampling<SamplingResult>(sampleExecutor);
		this.decisionProbes = decisionProbes;
	}
	
	public DecisionProbes sampleForBranchCvg(CfgNode node, OrCategoryCalculator preconditions)
			throws SavException {
		DecisionNodeProbe nodeProbe = decisionProbes.getNodeProbe(node);
		/*
		 * if all branches are missing, nothing we can do, and if all branches
		 * are covered, then do not need to do anything
		 */
		if (nodeProbe.areAllbranchesMissing()) {
			return decisionProbes;
		}
		
		CoveredBranches coveredType;
		int round = 1;
		DecisionProbes processedProbes = decisionProbes;
		do {
			coveredType = nodeProbe.getCoveredBranches();
			if (coveredType.isOneBranchMissing()) {
				SamplingResult samplingResult = selectiveSampling.selectDataForEmpty(nodeProbe, processedProbes.getOriginalVars(),
						preconditions, null, coveredType.getOnlyOneMissingBranch(), false);
				processedProbes = samplingResult.getDecisionProbes();
				/* update node probe */
				nodeProbe = processedProbes.getNodeProbe(node);
			}
		} while (round <= 2); // try 2 times to select sampling
		
		return processedProbes;
	}

	/**
	 * only run sampling if node is loop header and its true branch is covered.
	 */
	public DecisionProbes sampleForLoopCvg(CfgNode node,
			OrCategoryCalculator preconditions) throws SavException {
		DecisionNodeProbe nodeProbe = decisionProbes.getNodeProbe(node);
		if (node.isLoopHeader() || !nodeProbe.getCoveredBranches().coversTrue()
				|| (!nodeProbe.getOneTimeValues().isEmpty() && !nodeProbe.getMoreTimesValues().isEmpty())) {
			return decisionProbes;
		}
		BranchType missingBranch = nodeProbe.getMoreTimesValues().isEmpty() ? BranchType.TRUE
																			: BranchType.FALSE; /* ?? */
		SamplingResult samplingResult = selectiveSampling.selectDataForEmpty(nodeProbe, decisionProbes.getOriginalVars(),
				preconditions, null, missingBranch, false);
		return samplingResult.getDecisionProbes();
	}

	public SamplingResult sampleForModel(DecisionNodeProbe nodeProbe, List<ExecVar> originalVars,
			List<DataPoint> dataPoints, OrCategoryCalculator preconditions, List<Divider> learnedDividers)
			throws SavException {
		return selectiveSampling.selectDataForModel(nodeProbe, originalVars, dataPoints, preconditions, learnedDividers);
	}
}
