/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel.common;

/**
 * @author LLT
 *
 */
public class ExcelSettings {
	public static final int DATA_SHEET_HEADER_ROW_IDX = 0;
	public static int DEFAULT_MAX_ROW_PER_SHEET = 3000;
	private int maxRowPerSheet = DEFAULT_MAX_ROW_PER_SHEET;
	private String excelRootFolder;
	private String excelFilePrefix;
	private boolean appendLastFile;

	public ExcelSettings(String rootFolder, String filePrefix) {
		this.excelRootFolder = rootFolder;
		this.excelFilePrefix = filePrefix;
	}

	public ExcelSettings() {
	}

	public String getExcelRootFolder() {
		return excelRootFolder;
	}

	public void setExcelRootFolder(String excelRootFolder) {
		this.excelRootFolder = excelRootFolder;
	}

	public boolean isAppendLastFile() {
		return appendLastFile;
	}

	public void setAppendLastFile(boolean appendLastFile) {
		this.appendLastFile = appendLastFile;
	}

	public String getExcelFilePrefix() {
		return excelFilePrefix;
	}

	public void setExcelFilePrefix(String excelFilePrefix) {
		this.excelFilePrefix = excelFilePrefix;
	}

	public int getMaxRowPerSheet() {
		return maxRowPerSheet;
	}

	public void setMaxRowPerSheet(int maxRowPerSheet) {
		this.maxRowPerSheet = maxRowPerSheet;
	}

}
