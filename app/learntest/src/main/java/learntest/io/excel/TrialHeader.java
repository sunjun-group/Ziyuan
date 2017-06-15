/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.io.excel;

import learntest.io.excel.common.ExcelHeader;

/**
 * @author LLT
 *
 */
public enum TrialHeader implements ExcelHeader {
	METHOD_NAME("method name"),
	JDART_TIME("jdart time"),
	JDART_COVERAGE("jdart coverage"),
	JDART_TEST_CNT("jdart cnt"),
	L2T_TIME("l2t time"),
	L2T_COVERAGE("l2t coverage"),
	L2T_TEST_CNT("l2t test cnt"),
	RANDOOP_TIME("randoop time"),
	RANDOOP_COVERAGE("randoop coverage"),
	RANDOOP_TEST_CNT("randoop test cnt"),
	ADVANTAGE("advantage"),
	METHOD_LENGTH("method length"),
	METHOD_START_LINE("start line");
	
	private String title;

	private TrialHeader(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getCellIdx() {
		return ordinal();
	}
}
