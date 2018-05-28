/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import evosuite.core.commons.excel.ExcelHeader;
import evosuite.core.commons.excel.SimpleExcelReader;
import evosuite.core.commons.excel.SimpleExcelWriter;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class EvosuiteExcelHandler {
	public static String DATA_SHEET_NAME = "data";
	private EvoExcelReader excelReader;
	private EvoExcelWriter excelWriter;
	public static int methodNameColIdx = 0;
	public static int methodStartLineColIdx = 1;
	public static int evoStartIdx = 2;
	
	public EvosuiteExcelHandler(String fileName) throws Exception {
		File file = new File(fileName);
		excelReader = new EvoExcelReader(file);
		excelWriter = new EvoExcelWriter(file);
	}
	
	public void updateData(ExportData data) throws IOException {
		excelWriter.updateData(data);
	}
	
	public List<ExportData> readData() {
		return excelReader.getData();
	}
	
	private static class EvoExcelWriter extends SimpleExcelWriter<ExportData> {
		private boolean evoColAdded = false;
		public EvoExcelWriter(File file) throws Exception {
			super(file, Header.values());
		}

		@Override
		protected void addRowData(Row row, ExportData rowData) throws IOException {
			throw new UnsupportedOperationException();
		}
		
		public void updateData(ExportData data) throws IOException {
			/* add new column */
			if (!evoColAdded) {
				addHeader(Header.EVOSUITE_BRANCH_COVERAGE);
				addHeader(Header.EVOSUITE_COVERAGE_INFO);
				evoColAdded = true;
			}
			if (data.getEvoResult() != null) {
				addCell(dataSheet.getRow(data.getRowNum()), Header.EVOSUITE_BRANCH_COVERAGE, data.getEvoResult().branchCoverage);
				if (data.getEvoResult().coverageInfo != null) {
					addCell(dataSheet.getRow(data.getRowNum()), Header.EVOSUITE_COVERAGE_INFO, StringUtils.join(data.getEvoResult().coverageInfo, ";"));
				}
			} else {
				addCell(dataSheet.getRow(data.getRowNum()), Header.EVOSUITE_COVERAGE_INFO, "Evosuite execution error!");
			}
			writeWorkbook();
		}
	}

	static class EvoExcelReader extends SimpleExcelReader {
		public EvoExcelReader(File file) throws Exception {
			super(DATA_SHEET_NAME, Header.values(), file);
		}
		
		public List<ExportData> getData() {
			List<ExportData> data = new ArrayList<>();
			Iterator<Row> it = dataSheet.rowIterator();
			updateHeaderColIdx(it.next()); // ignore first row (header)
			while (it.hasNext()) {
				Row row = it.next();
				data.add(readDataSheetRow(row));
			}
			return data;
		}
		
		private void updateHeaderColIdx(Row row) {
			Iterator<Cell> it = row.cellIterator();	
			int i = 0;
			boolean evoExist = false;
			while(it.hasNext()) {
				Cell cell = it.next();
				String title = cell.getStringCellValue();
				if (Header.METHOD_NAME.getTitle().equals(title)) {
					methodNameColIdx = i;
				} else if (Header.METHOD_START_LINE.getTitle().equals(title)) {
					methodStartLineColIdx = i;
				} else if (Header.EVOSUITE_BRANCH_COVERAGE.getTitle().equals(title)) {
					evoExist = true;
					evoStartIdx = i;
				}
				i++;
			}
			if (!evoExist) {
				evoStartIdx = i;
			}
		}

		public ExportData readDataSheetRow(Row row) {
			ExportData data = new ExportData();
			data.setMethodName(getStringCellValue(row, Header.METHOD_NAME));
			data.setStartLine(getIntCellValue(row, Header.METHOD_START_LINE));
			data.setRowNum(row.getRowNum());
			Cell cell = getCell(row, Header.EVOSUITE_BRANCH_COVERAGE);
			data.setEvoCvgExisted(cell != null);
			return data;
		}
	}
	
	public static enum Header implements ExcelHeader {
		METHOD_NAME("method name"),
		METHOD_START_LINE("start line"),
		EVOSUITE_BRANCH_COVERAGE("evosuite coverage"),
		EVOSUITE_COVERAGE_INFO("evosuite coverage info");
		
		private String title;

		private Header(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		@Deprecated
		public int getCellIdx() {
			if (this == Header.EVOSUITE_BRANCH_COVERAGE) {
				return evoStartIdx;
			}
			if (this == Header.EVOSUITE_COVERAGE_INFO) {
				return evoStartIdx + 1;
			}
			if (this == Header.METHOD_NAME) {
				return methodNameColIdx;
			}
			if (this == Header.METHOD_START_LINE) {
				return methodStartLineColIdx;
			}
			return ordinal();
		}
	}
}
