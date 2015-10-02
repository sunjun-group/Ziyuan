/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

/**
 * @author LLT
 * 
 */
public enum SystemVariables {
	SAV_JUNIT_JAR("sav.junit.runner.jar"), 
	ENABLE_ASSERTION("assertion.enable", "true"),
	SLICE_COLLECT_VAR("slicing.collect.var", "false");

	private String name;
	private String defValue;

	private SystemVariables(String name) {
		this(name, null);
	}

	private SystemVariables(String name, String defValue) {
		this.name = name;
		this.defValue = defValue;
	}

	public String getName() {
		return name;
	}

	public String getDefValue() {
		return defValue;
	}
}
