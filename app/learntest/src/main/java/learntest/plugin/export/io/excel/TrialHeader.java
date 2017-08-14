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
	VALID_NUM("learn formulas iterations"),
	AVE_COVERAGE_ADV("average coverage advantage"),
	FIRST_TRIAL_R("1st trial ran"), /** 1st trial randoop coverage */
	FIRST_TRIAL_L2T("1st trial l2t"), /** 1st trial l2t coverage */
	FIRST_TRIAL_ADV("1st trial advantage"), 
	FIRST_TRIAL_L("1st trial learned"), /** 1st trial l2t learn formulas */
	
	SECOND_TRIAL_R("2nd trial ran"),
	SECOND_TRIAL_L2T("2nd trial l2t"),
	SECOND_TRIAL_ADV("2nd trial advantage"), 
	SECOND_TRIAL_L("2nd trial learned"),
	
	THIRD_TRIAL_R("3rd trial ran"),
	THIRD_TRIAL_L2T("3rd trial l2t"),
	THIRD_TRIAL_ADV("3rd trial advantage"), 
	THIRD_TRIAL_L("3rd trial learned"),
	
	FORTH_TRIAL_R("4th trial ran"),
	FORTH_TRIAL_L2T("4th trial l2t"),
	FORTH_TRIAL_ADV("4th trial advantage"), 
	FORTH_TRIAL_L("4th trial learned"),
	
	FIFTH_TRIAL_R("5th trial ran"),
	FIFTH_TRIAL_L2T("5th trial l2t"),
	FIFTH_TRIAL_ADV("5th trial advantage"), 
	FIFTH_TRIAL_L("5th trial learned"),

	FIRST_L2T_WORSE_THAN_RAND("1st l2t worse than randoop"),
	FIRST_RAND_WORSE_THAN_L2T("1st randoop worse than l2t"),
	SECOND_L2T_WORSE_THAN_RAND("2nd l2t worse than randoop"),
	SECOND_RAND_WORSE_THAN_L2T("2nd randoop worse than l2t"),
	THIRD_L2T_WORSE_THAN_RAND("3rd l2t worse than randoop"),
	THIRD_RAND_WORSE_THAN_L2T("3rd randoop worse than l2t"),
	FORTH_L2T_WORSE_THAN_RAND("4th l2t worse than randoop"),
	FORTH_RAND_WORSE_THAN_L2T("4th randoop worse than l2t"),
	FIFTH_L2T_WORSE_THAN_RAND("5th l2t worse than randoop"),
	FIFTH_RAND_WORSE_THAN_L2T("5th randoop worse than l2t")
	;
	
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
