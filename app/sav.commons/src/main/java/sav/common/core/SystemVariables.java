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
public enum SystemVariables implements ISystemVariable {
	SYS_SAV_JUNIT_JAR("sav.junit.runner.jar"), 
	APP_ENABLE_ASSERTION("assertion.enable", Boolean.TRUE),
	SLICE_COLLECT_VAR("slicing.collect.var", Boolean.FALSE),
	//SLICE_BKP_VAR_INHERIT: values([empty], BACKWARD, FORWARD);
	SLICE_BKP_VAR_INHERIT("slicing.collected.vars.inherit", ""),
	FAULT_LOCATE_USE_SLICE("fault.localization.use.slice", Boolean.TRUE),
	FAULT_LOCATE_SPECTRUM_ALGORITHM("fault.localization.spectrum.algorithm", "OCHIAI"),
	PROJECT_CLASSLOADER("project.classloader"),
	TESTCASE_TIMEOUT("testcase.running.timeout", -1l);
	
	private String name;
	private Object defValue;

	private SystemVariables(String name) {
		this(name, null);
	}

	private SystemVariables(String name, Object defValue) {
		this.name = name;
		this.defValue = defValue;
	}

	@Override
	public String getName() {
		return name;
	}

	public Object getDefValue() {
		return defValue;
	}
}
