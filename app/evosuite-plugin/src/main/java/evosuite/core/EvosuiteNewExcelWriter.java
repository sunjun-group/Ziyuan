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

import org.apache.poi.ss.usermodel.Row;

import evosuite.core.EvosuiteExcelHandler.Header;
import evosuite.core.commons.excel.SimpleExcelWriter;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class EvosuiteNewExcelWriter extends SimpleExcelWriter<ExportData> {

	public EvosuiteNewExcelWriter(File file) throws Exception {
		super(file, Header.values());
	}

	@Override
	protected void addRowData(Row row, ExportData rowData) throws IOException {
		addCell(row, Header.METHOD_NAME, rowData.getMethodName());
		addCell(row, Header.METHOD_START_LINE, rowData.getStartLine());
		if (rowData.getEvoResult() != null) {
			addCell(row, Header.EVOSUITE_BRANCH_COVERAGE, rowData.getEvoResult().branchCoverage);
			addCell(row, Header.EVOSUITE_COVERAGE_INFO, StringUtils.join(rowData.getEvoResult().coverageInfo, ";"));
		}
		writeWorkbook();
	}


}
