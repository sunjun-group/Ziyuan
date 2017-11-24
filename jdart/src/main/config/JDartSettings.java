/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package config;

import jdart.vm.JDartVmRunner;

/**
 * @author LLT
 *
 */
public class JDartSettings implements IJDartSettings {
	private static JDartSettings instance;

	@Override
	public String getRuntimeCP() {
		return JDartVmRunner.extractToTemp().getAbsolutePath();
	}

	@Override
	public String getProcessDirectory() {
		return null;
	}

	public static JDartSettings getInstance() {
		if (instance == null) {
			instance = new JDartSettings();
		}
		return instance;
	}
}
