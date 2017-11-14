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
	FIRST_L2T_WORSE_THAN_RAND("1st l2t worse than randoop"),
	FIRST_RAND_WORSE_THAN_L2T("1st randoop worse than l2t"),
	FIRST_SYMBOLIC_TIMES("1st symbolic times"),
	
	SECOND_TRIAL_R("2nd trial ran"),
	SECOND_TRIAL_L2T("2nd trial l2t"),
	SECOND_TRIAL_ADV("2nd trial advantage"), 
	SECOND_TRIAL_L("2nd trial learned"),
	SECOND_L2T_WORSE_THAN_RAND("2nd l2t worse than randoop"),
	SECOND_RAND_WORSE_THAN_L2T("2nd randoop worse than l2t"),
	SECOND_SYMBOLIC_TIMES("2nd symbolic times"),
	
	THIRD_TRIAL_R("3rd trial ran"),
	THIRD_TRIAL_L2T("3rd trial l2t"),
	THIRD_TRIAL_ADV("3rd trial advantage"), 
	THIRD_TRIAL_L("3rd trial learned"),
	THIRD_L2T_WORSE_THAN_RAND("3rd l2t worse than randoop"),
	THIRD_RAND_WORSE_THAN_L2T("3rd randoop worse than l2t"),
	THIRD_SYMBOLIC_TIMES("3rd symbolic times"),
	
	FORTH_TRIAL_R("4th trial ran"),
	FORTH_TRIAL_L2T("4th trial l2t"),
	FORTH_TRIAL_ADV("4th trial advantage"), 
	FORTH_TRIAL_L("4th trial learned"),
	FORTH_L2T_WORSE_THAN_RAND("4th l2t worse than randoop"),
	FORTH_RAND_WORSE_THAN_L2T("4th randoop worse than l2t"),
	FORTH_SYMBOLIC_TIMES("4th symbolic times"),
	
	FIFTH_TRIAL_R("5th trial ran"),
	FIFTH_TRIAL_L2T("5th trial l2t"),
	FIFTH_TRIAL_ADV("5th trial advantage"), 
	FIFTH_TRIAL_L("5th trial learned"),
	FIFTH_L2T_WORSE_THAN_RAND("5th l2t worse than randoop"),
	FIFTH_RAND_WORSE_THAN_L2T("5th randoop worse than l2t"),
	FIFTH_SYMBOLIC_TIMES("5th symbolic times"),
	
	EVOSUITECOV("evosuite coverage"),
	EVOSUITEINFO("evosuite coverage info"),
	
	FIRST_TRIAL_JDART("1st trial jdart"), /** 1st trial jdart coverage */
	FIRST_TRIAL_JDART_CNT("1st trial jdart cnt"),
	FIRST_TRIAL_JDART_SOLVE_TIMES("1st trial jdart solve times"),
	
	SECOND_TRIAL_JDART("2nd trial jdart"),
	SECOND_TRIAL_JDART_CNT("2nd trial jdart cnt"),
	SECOND_TRIAL_JDART_SOLVE_TIMES("2nd trial jdart solve times"),
	
	THIRD_TRIAL_JDART("3rd trial jdart"),
	THIRD_TRIAL_JDART_CNT("3rd trial jdart cnt"),
	THIRD_TRIAL_JDART_SOLVE_TIMES("3rd trial jdart solve times"),
	
	FORTH_TRIAL_JDART("4th trial jdart"),
	FORTH_TRIAL_JDART_CNT("4th trial jdart cnt"),
	FORTH_TRIAL_JDART_SOLVE_TIMES("4th trial jdart solve times"),
	
	FIFTH_TRIAL_JDART("5th trial jdart"),
	FIFTH_TRIAL_JDART_CNT("5th trial jdart cnt"),
	FIFTH_TRIAL_JDART_SOLVE_TIMES("5th trial jdart solve times"),
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
