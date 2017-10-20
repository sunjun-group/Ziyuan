/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import evosuite.EvosuiteRunner.EvosuiteResult;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class MappingExcelHandler extends EvosuiteExcelHandler {

	public MappingExcelHandler(String fileName) throws Exception {
		super(fileName);
	}

	public void map(String fromXlsFile) throws Exception {
		ExtEvoExcelReader excelReader = new ExtEvoExcelReader(new File(fromXlsFile));
		List<ExportData> exportDatas = excelReader.getData();
		Map<String, ExportData> map = new HashMap<String, ExportData>();
		for (ExportData data : exportDatas) {
			map.put(getMethodId(data), data);
		}
		List<ExportData> ziyuanData = super.readData();
		/* update evosuite coverage */
		for (ExportData data : ziyuanData) {
			ExportData exportData = map.get(getMethodId(data));
			if (exportData != null) {
				data.setEvoResult(exportData.getEvoResult());
			}
		}
		/* update excel */
		for (ExportData data : ziyuanData) {
			super.updateData(data);
		}
	}
	
	private String getMethodId(ExportData data) {
		return StringUtils.dotJoin(data.getMethodName(), data.getStartLine());
	}

	public static class ExtEvoExcelReader extends EvoExcelReader {

		public ExtEvoExcelReader(File file) throws Exception {
			super(file);
		}
		
		@Override
		public ExportData readDataSheetRow(Row row) {
			ExportData data = new ExportData();
			data.setMethodName(row.getCell(0).getStringCellValue());
			data.setStartLine((int) row.getCell(1).getNumericCellValue());
			Cell cell = row.getCell(2);
			if (cell != null) {
				EvosuiteResult evoResult = new EvosuiteResult();
				evoResult.branchCoverage = cell.getNumericCellValue();
				data.setEvoResult(evoResult);
			}
			return data;
		}
	}
}
