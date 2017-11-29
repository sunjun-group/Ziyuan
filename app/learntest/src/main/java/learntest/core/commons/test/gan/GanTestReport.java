/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.gan.vm.BranchDataSet;
import learntest.core.gan.vm.BranchDataSet.Category;

/**
 * @author LLT
 *
 */
public abstract class GanTestReport {

	public void onRoundResult(RunTimeInfo runtimeInfo) {
		// do nothing by default
	}
	
	public void startRound(int i, LearnTestParams params) {
		// do nothing by default
	}
	
	public void initCoverage(double firstCoverage, String cvgInfo) {
		// do nothing by default
	}
	
	public void accuracy(int nodeIdx, double acc, Category category) {
		// do nothing by default
	}

	public void trainingDatapoints(CfgNode node, DecisionBranchType branchType, BranchDataSet dataSet) {
		// do nothing by default
	}

	public void samplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult, DecisionBranchType branchType) {
		// do nothing by default
	}

	public void coverage(String cvgInfo, double cvg) {
		// do nothing by default
	}
}
