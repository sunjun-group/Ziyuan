/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import static learntest.plugin.export.io.excel.TrialHeader.L2T_COVERAGE;
import static learntest.plugin.export.io.excel.TrialHeader.L2T_TEST_CNT;
import static learntest.plugin.export.io.excel.TrialHeader.METHOD_LENGTH;
import static learntest.plugin.export.io.excel.TrialHeader.METHOD_START_LINE;
import static learntest.plugin.export.io.excel.TrialHeader.RANDOOP_COVERAGE;
import static learntest.plugin.export.io.excel.TrialHeader.RANDOOP_TEST_CNT;
import static learntest.plugin.export.io.excel.TrialHeader.RANDOOP_TIME;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import learntest.core.RunTimeInfo;
import learntest.core.commons.exception.LearnTestException;
import learntest.plugin.export.io.excel.common.ExcelReader;
import sav.common.core.utils.Assert;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class TrialExcelReader extends ExcelReader {
	
	private Sheet dataSheet;
	
	public TrialExcelReader() {
	}
	
	public TrialExcelReader(File file) throws Exception {
		super(file);
	}
	
	@Override
	public void reset(File file) throws Exception {
		super.reset(file);
		dataSheet = workbook.getSheet(TrialExcelConstants.DATA_SHEET_NAME);
		if (dataSheet == null) {
			throw new LearnTestException("invalid experimental file! (Cannot get data sheet)");
		}
	}
	
	public Map<String, Trial> readDataSheet() {
		Assert.assertNotNull(dataSheet, "TrialExcelReader has not initialized!");
		Iterator<Row> it = dataSheet.rowIterator();
		Row header = it.next(); // ignore first row (header)
		Assert.assertTrue(isDataSheetHeader(header), "Data sheet is invalid!");
		Map<String, Trial> data = new HashMap<String, Trial>();
		while (it.hasNext()) {
			Row row = it.next();
			readDataSheetRow(row, data);
		}
		return data;
	}
	
	public boolean hasValidHeader() {
		Row header = dataSheet.iterator().next();
		return isDataSheetHeader(header);
	}
	
	private void readDataSheetRow(Row row, Map<String, Trial> data) {
		Trial trial = new Trial();
		trial.setMethodName(getStringCellValue(row, TrialHeader.METHOD_NAME));

		RunTimeInfo jdartInfo = new RunTimeInfo();
		jdartInfo.setTime(getLongCellValue(row, TrialHeader.JDART_TIME));
		jdartInfo.setCoverage(getDoubleCellValue(row, TrialHeader.JDART_COVERAGE));
		jdartInfo.setTestCnt(getIntCellValue(row, TrialHeader.JDART_TEST_CNT));
		trial.setJdartRtInfo(jdartInfo);
		
		RunTimeInfo l2tInfo = new RunTimeInfo();
		l2tInfo.setTime(getLongCellValue(row, TrialHeader.L2T_TIME));
		l2tInfo.setCoverage(getDoubleCellValue(row, L2T_COVERAGE));
		l2tInfo.setTestCnt(getIntCellValue(row, L2T_TEST_CNT));
		trial.setL2tRtInfo(l2tInfo);
		
		RunTimeInfo randoopInfo = new RunTimeInfo();
		randoopInfo.setTime(getLongCellValue(row, RANDOOP_TIME));
		randoopInfo.setCoverage(getDoubleCellValue(row, RANDOOP_COVERAGE));
		randoopInfo.setTestCnt(getIntCellValue(row, RANDOOP_TEST_CNT));
		trial.setRanRtInfo(randoopInfo);
		
		trial.setMethodLength((int) getDoubleCellValue(row, METHOD_LENGTH));
		trial.setMethodStartLine(getIntCellValue(row, METHOD_START_LINE));
		data.put(StringUtils.join(TrialExcelConstants.METHOD_ID_SEPARATOR, trial.getMethodName(), trial.getMethodStartLine()), 
				trial);
	}

	private boolean isDataSheetHeader(Row header) {
		if (header.getRowNum() != TrialExcelConstants.DATA_SHEET_HEADER_ROW_IDX) {
			return false;
		}
		for (TrialHeader title : TrialHeader.values()) {
			if (!title.getTitle().equals(header.getCell(title.getCellIdx()).getStringCellValue())) {
				return false;
			}
		}
		return true;
	}

	public int getLastDataSheetRow() {
		return dataSheet.getLastRowNum();
	}
}
