/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.TestSettings;
import learntest.core.commons.test.TestTool;
import learntest.core.commons.test.gan.evaltrial.GanTrialReport;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gan.vm.NodeDataSet;
import learntest.core.gan.vm.NodeDataSet.Category;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class GanTestTool extends TestTool {
	private GanTestReport report;
	private Set<Integer> fullCoveredNodes;
	
	public GanTestTool() {
		try {
			fullCoveredNodes = new HashSet<>();
		} catch (Exception e) {
			log("cannot init test report: ", e.getMessage());
		}
	}
	
	@Override
	public void startMethod(String methodFullName) {
		try {
			report = new GanTrialReport(TestSettings.GAN_EXCEL_PATH);
			fullCoveredNodes.clear();
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
		log(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		report.initCoverage(firstCoverage);
		flush();
	}
	
	public void logCoverage(CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		log(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		flush();
	}
	
	public void logTrainDatapoints(CfgNode node, NodeDataSet dataSet) {
		if (!isEnable()) {
			return;
		}
		if (CollectionUtils.isNotEmpty(dataSet.getDataset().get(Category.TRUE))
				&& CollectionUtils.isNotEmpty(dataSet.getDataset().get(Category.FALSE))) {
			fullCoveredNodes.add(node.getIdx());
		}
		log("Training datapoints: ");
		logFormat("NodeIdx={}", dataSet.getNodeId());
		for (Category cat : Category.values()) {
			logFormat("{}: ", cat.name());
			log(TextFormatUtils.printCol(dataSet.getDataset().get(cat), "\n"));
		}
		flush();
		report.trainingDatapoints(node, dataSet);
	}
	
	public void logSamplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult,
			Category category) {
		logAccuracy(node, samplingResult, category);
		report.samplingResult(node, allDatapoints, samplingResult, category);
	}
	
	public void logAccuracy(CfgNode node, SamplingResult samplingResult, Category category) {
		INodeCoveredData newData = samplingResult.getNewData(node);
		int falseSize = CollectionUtils.getSize(newData.getFalseValues());
		int trueSize = CollectionUtils.getSize(newData.getTrueValues());
		int total = falseSize + trueSize;
		int accSize = (category == Category.TRUE ? trueSize : falseSize);
		report.accuracy(node.getIdx(), accSize / ((double) total), category);
	}
	
	public void logRoundResult(RunTimeInfo runtimeInfo, int i) {
		if (!isEnable()) {
			return;
		}
		log("\n\nResult Round ", i);
		logRuntimeInfo(runtimeInfo, true);
		report.onRoundResult(runtimeInfo);
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
