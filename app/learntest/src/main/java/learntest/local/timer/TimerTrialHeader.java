package learntest.local.timer;

import learntest.plugin.export.io.excel.common.ExcelHeader;

public enum TimerTrialHeader implements ExcelHeader {
	METHOD_NAME("method name"),
	JDART_TIME("jdart time"),
	JDART_COVERAGE("jdart coverage"),
	JDART_TEST_CNT("jdart test cnt"),
	L2T_TIME("l2t time"),
	L2T_COVERAGE("l2t coverage"),
	L2T_TEST_CNT("l2t test cnt"),
	RANDOOP_TIME("randoop time"),
	RANDOOP_COVERAGE("randoop coverage"),
	RANDOOP_TEST_CNT("randoop test cnt"),
	ADVANTAGE("advantage"),
	METHOD_LENGTH("method length"),
	METHOD_START_LINE("start line"),
	TRIAL_L("trial learned"), 
	L2T_WORSE_THAN_RAND("l2t worse than randoop"),
	RAND_WORSE_THAN_L2T("randoop worse than l2t"),
	L2T_WORSE_THAN_RAND_B("l2t worse than randoop branches"),
	RAND_WORSE_THAN_L2T_B("randoop worse than l2t branches"),
	L2T_TIMELINE("l2t time line"),
	RANDOOP_TIMELINE("randoop time line"),
	L2T_S1("l2t coverage in step 1"),
	RANDOOP_S1("randoop coverage in step 1"),
	L2T_S2("l2t coverage in step 2"),
	RANDOOP_S2("randoop coverage in step 2"),
	L2T_S3("l2t coverage in step 3"),
	RANDOOP_S3("randoop coverage in step 3"),
	L2T_S4("l2t coverage in step 4"),
	RANDOOP_S4("randoop coverage in step 4"),
	L2T_S5("l2t coverage in step 5"),
	RANDOOP_S5("randoop coverage in step 5"),
	L2T_S6("l2t coverage in step 6"),
	RANDOOP_S6("randoop coverage in step 6"),
	L2T_S7("l2t coverage in step 7"),
	RANDOOP_S7("randoop coverage in step 7"),
	L2T_S8("l2t coverage in step 8"),
	RANDOOP_S8("randoop coverage in step 8"),
	L2T_S9("l2t coverage in step 9"),
	RANDOOP_S9("randoop coverage in step 9"),	
	VAR_TYPE("if all primitive type"),
	;
	
	private String title;

	private TimerTrialHeader(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getCellIdx() {
		return ordinal();
	}
}
