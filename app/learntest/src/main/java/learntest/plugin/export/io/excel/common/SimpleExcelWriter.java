/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel.common;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import learntest.plugin.export.io.excel.TrialExcelConstants;

/**
 * @author LLT
 *
 */
public abstract class SimpleExcelWriter<T> extends ExcelWriter {
	private ExcelHeader[] headers;
	protected Sheet dataSheet;
	private int lastDataSheetRow;
	private int headerRowIdx = ExcelSettings.DEFAULT_HEADER_ROW_IDX;

	public SimpleExcelWriter(File file, ExcelHeader[] headers) throws Exception {
		super();
		this.headers = headers;
		super.reset(file);
	}

	@Override
	protected void initFromNewFile(File file) {
		super.initFromNewFile(file);
		lastDataSheetRow = headerRowIdx - 1;
		dataSheet = createSheet(TrialExcelConstants.DATA_SHEET_NAME, 
				headers, ++ lastDataSheetRow);
	}
	
	@Override
	protected void initFromExistingFile(File file) throws Exception {
		super.initFromExistingFile(file);
		dataSheet = workbook.getSheet(TrialExcelConstants.DATA_SHEET_NAME);
		lastDataSheetRow = dataSheet.getLastRowNum();
	}
	
	protected Row getRow(int rowNum){
		return dataSheet.getRow(rowNum);
	}
	
	public void addHeader(ExcelHeader header) {
		addCell(getRow(headerRowIdx), header, header.getTitle());
	}
	
	public void addHeader(int colIdx, String title) {
		addCell(getRow(headerRowIdx), colIdx, title);
	}
	
	protected Row newDataSheetRow() {
		return super.newDataSheetRow(dataSheet, ++ lastDataSheetRow);
	}

	public int addRowData(T rowData) throws IOException {
		Row row = newDataSheetRow();
		addRowData(row, rowData);
		return lastDataSheetRow;
	}

	protected abstract void addRowData(Row row, T rowData) throws IOException;
	
	public void setHeaderRowIdx(int headerRowIdx) {
		this.headerRowIdx = headerRowIdx;
	}
}
