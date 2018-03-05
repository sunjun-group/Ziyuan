/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.local.timer;

import static learntest.local.timer.TimerTrialHeader.*;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

import learntest.core.RunTimeInfo;
import learntest.plugin.export.io.excel.Trial;
import learntest.plugin.export.io.excel.TrialExcelConstants;
import learntest.plugin.export.io.excel.common.SimpleExcelReader;
import sav.common.core.utils.Assert;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class TimerTrialExcelReader extends SimpleExcelReader {
	
	public TimerTrialExcelReader() {
		super(TrialExcelConstants.DATA_SHEET_NAME, TimerTrialHeader.values());
	}
	
	public TimerTrialExcelReader(File file) throws Exception {
		super(TrialExcelConstants.DATA_SHEET_NAME, TimerTrialHeader.values());
		reset(file);
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
	
	private void readDataSheetRow(Row row, Map<String, Trial> data) {
		Trial trial = new Trial();
		trial.setMethodName(getStringCellValue(row, TimerTrialHeader.METHOD_NAME));
		
		RunTimeInfo l2tInfo = new RunTimeInfo();
		l2tInfo.setTime(getLongCellValue(row, TimerTrialHeader.L2T_TIME));
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

	public int getLastDataSheetRow() {
		return dataSheet.getLastRowNum();
	}
}
