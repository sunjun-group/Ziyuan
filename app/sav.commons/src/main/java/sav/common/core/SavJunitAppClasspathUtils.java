/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.junit.SavJunitRunner;


/**
 * @author LLT
 *
 */
public class SavJunitAppClasspathUtils {
	public static final String SAV_JUNIT_JAR = "sav.junit.runner.jar";
	public static final String ENABLE_ASSERTION = "assertion.enable";
	
	private SavJunitAppClasspathUtils(){}
	
	public static String updateSavJunitJarPath(AppJavaClassPath appClasspath) {
		String jarPath = appClasspath.getVariable(SAV_JUNIT_JAR);
		if (jarPath == null) {
			jarPath = SavJunitRunner.extractToTemp().getAbsolutePath();
			appClasspath.setVariable(SAV_JUNIT_JAR, jarPath);
		}
		return jarPath;
	}
	
	public static boolean updateEnableAssertionSetting(
			AppJavaClassPath appClasspath, boolean defaultValue) {
		boolean result = defaultValue;
		String ea = appClasspath.getVariable(ENABLE_ASSERTION);
		if (ea == null) {
			appClasspath.setVariable(ENABLE_ASSERTION, String.valueOf(defaultValue));
		} else {
			result = Boolean.valueOf(ea);
		}
		return result;
	}
}
