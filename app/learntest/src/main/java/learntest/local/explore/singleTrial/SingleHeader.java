/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.local.explore.singleTrial;

import learntest.plugin.export.io.excel.common.ExcelHeader;

/**
 * @author LLT
 *
 */
public enum SingleHeader implements ExcelHeader {
	TRIAL("Trial"),
	L2T_COVERAGE("l2t coverage"),
	RANDOOP_COVERAGE("randoop coverage"),
	JDART_COVERAGE("jdart coverage"),
	LEARNSTATE("learned"),
	L2T_TIME("l2t time"),
	RAND_TIME("rand time"),
//	L2T_30("l2t coverage in 30s"),
//	RANDOOP_30("randoop coverage in 30s"),
//	L2T_60("l2t coverage in 60s"),
//	RANDOOP_60("randoop coverage in 60s"),
//	L2T_90("l2t coverage in 90s"),
//	RANDOOP_90("randoop coverage in 90s"),
//	L2T_120("l2t coverage in 120s"),
//	RANDOOP_120("randoop coverage in 120s"),
//	L2T_150("l2t coverage in 150s"),
//	RANDOOP_150("randoop coverage in 150s"),
	;
	
	private String title;

	private SingleHeader(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getCellIdx() {
		return ordinal();
	}
}
