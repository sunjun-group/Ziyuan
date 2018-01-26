/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.local.explore.timeTrial;

import learntest.plugin.export.io.excel.common.ExcelHeader;

/**
 * @author LLT
 *
 */
public enum TimeHeader implements ExcelHeader {
	METHOD("method"),
	L2T_ADV_TIME("l2t avg time"),
	RANDOOP_ADV_TIME("randoop avg time")
	;
	
	private String title;

	private TimeHeader(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getCellIdx() {
		return ordinal();
	}
}
