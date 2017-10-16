/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import org.apache.commons.lang.StringUtils;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.test.TestTool;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gan.vm.NodeDataSet;
import learntest.core.gan.vm.NodeDataSet.Category;
import learntest.plugin.export.io.excel.common.ExcelSettings;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class GanTestTool extends TestTool {
	private GanTestReport report;
	private GanTrial trial;
	
	public GanTestTool() {
		try {
			ExcelSettings settings = new ExcelSettings();
			report = new GanTestReport(settings);
		} catch (Exception e) {
			log("cannot init test report: ", e.getMessage());
		}
	}
	
	@Override
	public void logFirstCoverage(double firstCoverage, CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		logFormat("First covearge: {}", firstCoverage);
		log(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		flush();
	}
	
	public void logCoverage(CfgCoverage cfgCoverage) {
		if (!isEnable()) {
			return;
		}
		log(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		flush();
	}
	
	public void logDatapoints(NodeDataSet dataSet) {
		if (!isEnable()) {
			return;
		}
		log("Generated datapoints: ");
		logFormat("NodeIdx={}", dataSet.getNodeId());
		for (Category cat : Category.values()) {
			logFormat("{}: ", cat.name());
			log(TextFormatUtils.printCol(dataSet.getDataset().get(cat), "\n"));
		}
		flush();
	}
	
	public void logRoundResult(RunTimeInfo runtimeInfo, int i) {
		if (!isEnable()) {
			return;
		}
		log("\n\nResult Round ", i);
		logRuntimeInfo(runtimeInfo, true);
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

	public void logAverageResult(RunTimeInfo averageInfo, double bestCoverage) {
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
