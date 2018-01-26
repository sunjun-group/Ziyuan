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
