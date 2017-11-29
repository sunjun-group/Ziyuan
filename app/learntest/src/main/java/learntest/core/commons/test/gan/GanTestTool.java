/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.TestSettings;
import learntest.core.commons.test.TestTool;
import learntest.core.commons.test.gan.eval102.GanTestReport102;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gan.vm.BranchDataSet;
import learntest.core.gan.vm.BranchDataSet.Category;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class GanTestTool extends TestTool {
	private GanTestReport report;
	
	public GanTestTool() {
		try {
		} catch (Exception e) {
			log("cannot init test report: ", e.getMessage());
		}
	}
	
	@Override
	public void startMethod(String methodFullName) {
		try {
			report = new GanTestReport102(TestSettings.GAN_EXCEL_PATH_102);
		} catch (Exception e) {
			log("cannot init test report: ", e.getMessage());
		}
	}
	
	@Override
	public void startRound(int i, LearnTestParams params) {
		super.startRound(i, params);
		report.startRound(i, params);
	}
	
	@Override
	public void logFirstCoverage(double firstCoverage, CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		logFormat("First covearge: {}", firstCoverage);
		String cvgInfo = CoverageUtils.getBranchCoverageDisplayText(cfgCoverage);
		log(cvgInfo);
		report.initCoverage(firstCoverage, cvgInfo);
		flush();
	}
	
	public void logCoverage(CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		String cvgInfo = CoverageUtils.getBranchCoverageDisplayText(cfgCoverage);
		double cvg = CoverageUtils.calculateCoverageByBranch(cfgCoverage);
		log(cvgInfo);
		report.coverage(cvgInfo, cvg);
		flush();
	}
	
	public void logTrainDatapoints(CfgNode node, DecisionBranchType branchType, BranchDataSet dataSet) {
		if (!isEnable()) {
			return;
		}
		log("Training datapoints: ");
		logFormat("NodeIdx={}", dataSet.getNodeId());
		for (Category cat : Category.values()) {
			logFormat("{}: ", cat.name());
			log(TextFormatUtils.printCol(dataSet.getDataset().get(cat), "\n"));
		}
		flush();
		report.trainingDatapoints(node, branchType, dataSet);
	}
	
	public void logSamplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult,
			DecisionBranchType branchType) {
		report.samplingResult(node, allDatapoints, samplingResult, branchType);
	}
	
	public void logRoundResult(RunTimeInfo runtimeInfo, int i) {
		if (!isEnable()) {
			return;
		}
		log("\n\nResult Round ", i);
		logRuntimeInfo(runtimeInfo, true);
		report.onRoundResult(runtimeInfo);
		if (runtimeInfo != null) {
			report.coverage(runtimeInfo.getCoverageInfo(), runtimeInfo.getCoverage());
		}
	}
	
	private void logRuntimeInfo(RunTimeInfo runtimeInfo, boolean flush) {
		if (!isEnable()) {
			return;
		}
		if (runtimeInfo == null) {
			return;
		}
		if (runtimeInfo.getLineCoverageResult() != null) {
			log("Line coverage result:");
			log(runtimeInfo.getLineCoverageResult().getDisplayText());
		}
		logFormat("{} RESULT:", StringUtils.upperCase(LearnTestApproach.GAN.getName()));
		logFormat("TIME: {}; COVERAGE: {}; CNT: {}", TextFormatUtils.printTimeString(runtimeInfo.getTime()),
				runtimeInfo.getCoverage(), runtimeInfo.getTestCnt());
		logFormat("TOTAL COVERAGE INFO: \n{}", runtimeInfo.getCoverageInfo());
		flush(flush);
	}
	
	@Override
	protected String getModuleName() {
		return "Gan";
	}

	public void endMethod(RunTimeInfo averageInfo, double bestCoverage) {
		if (!isEnable()) {
			return;
		}
		log("------------- average result ------------------------");
		logRuntimeInfo(averageInfo, false);
		log("-------------------------------------");
		log("bestcoverage: {}", bestCoverage);
		flush();
	}


}
