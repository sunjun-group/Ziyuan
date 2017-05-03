/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto;

/**
 * @author LLT
 *
 */
public enum TestResultType {
	PASS,
	FAIL,
	UNKNOWN, // exception
	UNCOVERED; // target method is uncovered.
	
	public static TestResultType of(boolean isPass) {
		if (isPass) {
			return PASS;
		}
		return FAIL;
	}
}
