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
import learntest.plugin.export.io.excel.TrialHeader;

/**
 * @author LLT
 *
 */
public abstract class SimpleExcelWriter<T> extends ExcelWriter {
	private Sheet dataSheet;
	private int lastDataSheetRow;

	public SimpleExcelWriter(File file) throws Exception {
		super(file);
	}

	@Override
	protected void initFromNewFile(File file) {
		super.initFromNewFile(file);
		lastDataSheetRow = TrialExcelConstants.DATA_SHEET_HEADER_ROW_IDX - 1;
		dataSheet = createSheet(TrialExcelConstants.DATA_SHEET_NAME, 
				TrialHeader.values(), ++ lastDataSheetRow);
	}
	
	@Override
	protected void initFromExistingFile(File file) throws Exception {
		super.initFromExistingFile(file);
		dataSheet = workbook.getSheet(TrialExcelConstants.DATA_SHEET_NAME);
		lastDataSheetRow = dataSheet.getLastRowNum();
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
}
