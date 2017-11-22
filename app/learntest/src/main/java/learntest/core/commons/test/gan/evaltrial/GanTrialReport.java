/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan.evaltrial;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.core.LearnTestParams;
import learntest.core.RunTimeInfo;
import learntest.core.commons.data.decision.INodeCoveredData;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.test.gan.GanExportData;
import learntest.core.commons.test.gan.GanTestReport;
import learntest.core.gan.vm.NodeDataSet.Category;
import learntest.plugin.export.io.excel.common.ExcelHeader;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GanTrialReport extends GanTestReport {
	private Logger log = LoggerFactory.getLogger(GanTrialReport.class);
	private Map<String, GanExportData> exportDataMap = new HashMap<>();
	private GanTestExcelWriter excelWriter;
	private GanTrial trial;

	public GanTrialReport(String fileName) throws Exception {
		excelWriter = new GanTestExcelWriter(new File(fileName));
		trial = new GanTrial();
	}
		
	public void reset() {
		exportDataMap.clear();
	}
	
	public void startRound(int i, LearnTestParams params) {
		trial = new GanTrial();
		trial.setMethodId(params.getTargetMethod().getMethodId());
		trial.setSampleSize(params.getInitialTcTotal());
	}
	
	@Override
	public void initCoverage(double firstCoverage, String cvgInfo) {
		trial.setInitCoverage(firstCoverage);
	}
	
	@Override
	public void samplingResult(CfgNode node, List<double[]> allDatapoints, SamplingResult samplingResult,
			Category category) {
		INodeCoveredData newData = samplingResult.getNewData(node);
		int falseSize = CollectionUtils.getSize(newData.getFalseValues());
		int trueSize = CollectionUtils.getSize(newData.getTrueValues());
		int total = falseSize + trueSize;
		int accSize = (category == Category.TRUE ? trueSize : falseSize);
		trial.updateAcc(node.getIdx(), accSize / ((double) total));
	}
	
	@Override
	public void onRoundResult(RunTimeInfo runtimeInfo) {
		trial.setDecsNodeCvgInfo(runtimeInfo.getCoverageInfo());
		trial.setCoverage(runtimeInfo.getCoverage());
		try {
			export(trial);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}
	
	public void export(GanTrial trial) throws IOException {
		String methodId = trial.getMethodId();
		GanExportData exportData = exportDataMap.get(methodId);
		if (exportData == null) {
			exportData = new GanExportData();
			exportData.setMethodId(methodId);
			exportDataMap.put(methodId, exportData);
		}
		exportData.setTrial(trial);
		excelWriter.export(exportData);
	}

	private static class GanTestExcelWriter extends SimpleExcelWriter<GanExportData> {
		
		public GanTestExcelWriter(File file) throws Exception {
			super(file, Header.values());
		}
		
		public void export(GanExportData data) throws IOException {
			if (data.getRowNum() < 0) {
				addRowData(data);
			} else {
				addRowData(getRow(data.getRowNum()), data);
			}
			writeWorkbook();
		}

		@Override
		protected void addRowData(Row row, GanExportData data) throws IOException {
			addCell(row, Header.METHOD_ID, data.getId());
			addCell(row, Header.INIT_COVERAGES, data.getInitCvgs());
			addCell(row, Header.COVERAGES, data.getCvgs());
			int lastCol = data.getLastColNum() < 0 ? Header.COVERAGES.getCellIdx() : data.getLastColNum();
			addHeader(++lastCol, String.format("Accuracy-%d samples", data.getTrial().getSampleSize()));
			addCell(row, lastCol, data.getGanAccuracyStr());
			addHeader(++lastCol, String.format("Coverage info-%d samples", data.getTrial().getSampleSize()));
			addCell(row, lastCol, data.getTrial().getDecsNodeCvgInfo().replaceAll("\n", "\r\n"));
			data.setLastColNum(lastCol);
			data.setRowNum(row.getRowNum());
		}
	};
	
	private static enum Header implements ExcelHeader {
		METHOD_ID("method ID"),
		INIT_COVERAGES("initial coverage"),
		COVERAGES("coverages");
		
		private String title;
		private Header(String title) {
			this.title = title;
		}
		
		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public int getCellIdx() {
			return ordinal();
		}
		
	}

}
