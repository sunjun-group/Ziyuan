/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import learntest.plugin.export.io.excel.common.ExcelHeader;

/**
 * @author LLT
 *
 */
public enum TrialHeader implements ExcelHeader {
	METHOD_NAME("method name"),
	JDART_TIME("jdart time"),
	JDART_COVERAGE("jdart coverage"),
	JDART_TEST_CNT("jdart cnt"),
	L2T_TIME("l2t avg time"),
	L2T_COVERAGE("l2t avg coverage"),
	L2T_TEST_CNT("l2t avg test cnt"),
	L2T_BEST_COVERAGE("l2t best coverage"),
	RANDOOP_TIME("randoop avg time"),
	RANDOOP_COVERAGE("randoop avg coverage"),
	RANDOOP_TEST_CNT("randoop avg test cnt"),
	RANDOOP_BEST_COVERAGE("randoop best coverage"),
	ADVANTAGE("advantage"),
	METHOD_LENGTH("method length"),
	METHOD_START_LINE("start line"),
	L2T_VALID_COVERAGE("l2t valid coverage"),
	RANDOOP_VALID_COVERAGE("randoop valid coverage"),
	VALID_COVERAGE_ADV("valid average coverage advantage"),
	VALID_NUM("learn formulas iterations");
	
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
