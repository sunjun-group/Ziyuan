/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.breakpoint.data.DecisionLocation;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.data.DecisionNodeProbe;
import learntest.core.data.DecisionProbes;
import learntest.core.machinelearning.ITestCaseExecutor;
import learntest.testcase.data.BreakpointData;

/**
 * @author LLT
 * this class holds all shared service and main data for learning process. 
 * The services should not care about how to run each other, and let the mediator take in to account that 
 * job.
 * 
 */
public class LearningMediator {
	/* services */
	private ITestCaseExecutor tcExecutor;
	private CfgJaCoCo cfgJacoco;
	
	/* major data object */
	private DecisionProbes decisionProbes;
	
	/**
	 * try to run testcases with new selected input for target method.
	 * @param list
	 * @return
	 */
	public Map<DecisionLocation, BreakpointData> runSamples(List<Map<String, Object>> list) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * after running selective sampling, decision coverages will be updated
	 * based on running new testcases which are generated based on new sample
	 * data.
	 * 
	 * @param node
	 * @param labels
	 * @param preconditions
	 * @param object
	 * @param notCoveredBranch
	 * @param b
	 * @return 
	 */
	public DecisionNodeProbe selectiveSamplingForEmpty(CfgNode node, List<String> labels, OrCategoryCalculator preconditions,
			Object object, boolean trueBranch, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the decisionCoverages
	 */
	public DecisionProbes getDecisionProbes() {
		return decisionProbes;
	}
}
