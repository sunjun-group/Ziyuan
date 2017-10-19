/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import evosuite.commons.excel.ExcelHeader;
import evosuite.commons.excel.SimpleExcelReader;
import evosuite.commons.excel.SimpleExcelWriter;

/**
 * @author LLT
 *
 */
public class EvosuiteExcelHandler {
	public static String DATA_SHEET_NAME = "data";
	private EvoExcelReader excelReader;
	private EvoExcelWriter excelWriter;
	private static int methodNameColIdx;
	private static int methodStartLineColIdx;
	private static int evoStartIdx;
	
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
			addCell(dataSheet.getRow(data.getRowNum()), Header.EVOSUITE_BRANCH_COVERAGE, data.getEvoResult().branchCoverage);
			addCell(dataSheet.getRow(data.getRowNum()), Header.EVOSUITE_COVERAGE_INFO, data.getEvoResult().coverageInfo);
			writeWorkbook();
		}
	}

	private static class EvoExcelReader extends SimpleExcelReader {
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
			while(it.hasNext()) {
				Cell cell = it.next();
				String title = cell.getStringCellValue();
				if (Header.METHOD_NAME.getTitle().equals(title)) {
					methodNameColIdx = i;
				} else if (Header.METHOD_START_LINE.getTitle().equals(title)) {
					methodStartLineColIdx = i;
				} else if (Header.EVOSUITE_BRANCH_COVERAGE.getTitle().equals(title)) {
					evoStartIdx = i;
				}
				i++;
			}
			if (evoStartIdx == 0) {
				evoStartIdx = i;
			}
		}

		public ExportData readDataSheetRow(Row row) {
			ExportData data = new ExportData();
			data.setMethodName(getStringCellValue(row, Header.METHOD_NAME));
			data.setStartLine(getIntCellValue(row, Header.METHOD_START_LINE));
			data.setRowNum(row.getRowNum());
			return data;
		}
	}
	
	private static enum Header implements ExcelHeader {
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
