/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons;


import sav.common.core.utils.StringUtils;
import sav.commons.utils.ConfigUtils;

/**
 * @author LLT
 * 
 */
public class TestConfiguration {
	private static TestConfiguration config;
	private static final String junitCore = "org.junit.runner.JUnitCore";
	public String TRUNK;
	public String junitLib;
	public String javaSlicerPath;
	public String testTarget;
	// do not remove this one, this is not the current java home of the
	// application
	public String javaHome;

	private TestConfiguration() {
		TRUNK = ConfigUtils.getTrunkPath();
		junitLib = TRUNK + "/app/icsetlv/src/test/lib/*";
		testTarget = getTestTarget("sav.commons");
		//TODO: just by default, will load from test_configuration.properties.
		javaHome = ConfigUtils.getJavaHome();
	}
	
	public String getTestScrPath(String module) {
		return StringUtils.join("", TRUNK, "/app/", module, "/src/test/java");
	}

	public String getTestTarget(String module) {
		return StringUtils.join("", TRUNK, "/app/", module, "/target/test-classes");
	}

	public String getTarget(String module) {
		return StringUtils.join("", TRUNK, "/app/", module, "/target/classes");
	}

	public static TestConfiguration getInstance() {
		if (config == null) {
			config = new TestConfiguration();
		}
		return config;
	}

	public String getJunitcore() {
		return junitCore;
	}

	public String getJavaBin() {
		return javaHome + "/bin";
	}

	public String getJunitLib() {
		return junitLib;
	}

	public static String getTrunk() {
		return getInstance().TRUNK;
	}
}
