/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package commons;

import sav.common.core.utils.ConfigUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class TestConfiguration {
	public static final String ICSETLV = "icsetlv";
	public static final String FALTLOCALISATION = "faultLocalization";

	private static TestConfiguration config;
	private static final String junitCore = "org.junit.runner.JUnitCore";
	public String TRUNK;
	public String junitLib;
	public String javaSlicerPath;

	private TestConfiguration() {
		TRUNK = ConfigUtils.getTrunkPath();
		junitLib = TRUNK + "/app/icsetlv/src/test/lib/*";
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
		return ConfigUtils.getJavaHome() + "/bin";
	}

	public String getJunitLib() {
		return junitLib;
	}

	public static String getTrunk() {
		return getInstance().TRUNK;
	}
}
