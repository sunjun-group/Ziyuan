/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

import learntest.plugin.export.io.excel.common.ExcelHeader;
import learntest.plugin.export.io.excel.common.SimpleExcelWriter;

/**
 * @author LLT
 *
 */
public class GanTestReport {
	private Map<String, GanExportData> exportDataMap = new HashMap<>();
	private GanTestExcelWriter excelWriter;

	public GanTestReport(String fileName) throws Exception {
		excelWriter = new GanTestExcelWriter(new File(fileName));
	}
		
	public void reset() {
		exportDataMap.clear();
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
			addCell(row, Header.METHOD_ID, data.getMethodId());
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
