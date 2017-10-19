/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.commons.excel;

import java.io.File;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import sav.common.core.SavRtException;

/**
 * @author LLT
 *
 */
public class SimpleExcelReader extends ExcelReader {
	private String sheetName;
	protected Sheet dataSheet;
	private ExcelHeader[] headers;
	private int headerRowIdx = ExcelSettings.DEFAULT_HEADER_ROW_IDX;
	
	public SimpleExcelReader(String sheetName, ExcelHeader[] headers) {
		init(sheetName, headers);
	}

	private void init(String sheetName, ExcelHeader[] headers) {
		this.sheetName = sheetName;
		this.headers = headers;
	}
	
	public SimpleExcelReader(String sheetName, ExcelHeader[] headers, File file) throws Exception {
		this(sheetName, headers);
		reset(file);
	}
	
	@Override
	public void reset(File file) throws Exception {
		super.reset(file);
		dataSheet = workbook.getSheet(sheetName);
		if (dataSheet == null) {
			throw new SavRtException("invalid experimental file! (Cannot get data sheet)");
		}
	}
	
	public boolean hasValidHeader() {
		Row header = dataSheet.iterator().next();
		return isDataSheetHeader(header);
	}
	
	protected boolean isDataSheetHeader(Row header) {
		if (header.getRowNum() != headerRowIdx) {
			return false;
		}
		for (ExcelHeader title : headers) {
			if (!title.getTitle().equals(header.getCell(title.getCellIdx()).getStringCellValue())) {
				return false;
			}
		}
		return true;
	}

	public int getLastDataSheetRow() {
		return dataSheet.getLastRowNum();
	}

	public void setHeaderRowIdx(int headerRowIdx) {
		this.headerRowIdx = headerRowIdx;
	}
}
