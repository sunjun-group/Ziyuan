/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.TestSettings;
import learntest.core.commons.test.TestTool;
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
	private Logger log = LoggerFactory.getLogger(GanTestTool.class);
	private GanTestReport report;
	private GanTrial trial;
	private Set<Integer> fullCoveredNodes;
	
	public GanTestTool() {
		try {
			fullCoveredNodes = new HashSet<>();
			trial = new GanTrial();
		} catch (Exception e) {
			log("cannot init test report: ", e.getMessage());
		}
	}
	
	@Override
	public void startMethod(String methodFullName) {
		try {
			report = new GanTestReport(TestSettings.GAN_EXCEL_PATH);
			fullCoveredNodes.clear();
		} catch (Exception e) {
			log("cannot init test report: ", e.getMessage());
		}
	}
	
	@Override
	public void startRound(int i, LearnTestParams params) {
		super.startRound(i, params);
		trial = new GanTrial();
		trial.setMethodId(params.getTargetMethod().getMethodId());
		trial.setSampleSize(params.getInitialTcTotal());
	}
	
	@Override
	public void logFirstCoverage(double firstCoverage, CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		logFormat("First covearge: {}", firstCoverage);
		log(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		trial.setInitCoverage(firstCoverage);
		flush();
	}
	
	public void logCoverage(CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		log(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		flush();
	}
	
	public void logDatapoints(int nodeIdx, NodeDataSet dataSet) {
		if (!isEnable()) {
			return;
		}
		if (CollectionUtils.isNotEmpty(dataSet.getDataset().get(Category.TRUE))
				&& CollectionUtils.isNotEmpty(dataSet.getDataset().get(Category.FALSE))) {
			fullCoveredNodes.add(nodeIdx);
		}
		log("Generated datapoints: ");
		logFormat("NodeIdx={}", dataSet.getNodeId());
		for (Category cat : Category.values()) {
			logFormat("{}: ", cat.name());
			log(TextFormatUtils.printCol(dataSet.getDataset().get(cat), "\n"));
		}
		flush();
	}
	
	public void logAccuracy(CfgNode node, SamplingResult samplingResult, Category category) {
		INodeCoveredData newData = samplingResult.getNewData(node);
		int falseSize = CollectionUtils.getSize(newData.getFalseValues());
		int trueSize = CollectionUtils.getSize(newData.getTrueValues());
		int total = falseSize + trueSize;
		int accSize = (category == Category.TRUE ? trueSize : falseSize);
		trial.updateAcc(node.getIdx(), accSize / ((double) total));
	}
	
	public void logRoundResult(RunTimeInfo runtimeInfo, int i) {
		if (!isEnable()) {
			return;
		}
		log("\n\nResult Round ", i);
		logRuntimeInfo(runtimeInfo, true);
		trial.setDecsNodeCvgInfo(runtimeInfo.getCoverageInfo());
		trial.setCoverage(runtimeInfo.getCoverage());
		try {
			report.export(trial);
		} catch (Exception e) {
			log.debug(e.getMessage());
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
