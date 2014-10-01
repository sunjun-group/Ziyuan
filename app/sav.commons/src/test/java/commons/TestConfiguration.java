/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package commons;

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
	public String javaHome = System.getProperty("java.home");
	public String TRUNK;
	public String junitLib;
	public String tracerLibPath;
	public String javaSlicerPath;
	
	private TestConfiguration() {
		TRUNK = System.getProperty("TZUYU_HOME");
		junitLib = TRUNK + "/app/icsetlv/src/test/lib/*";
		tracerLibPath = TRUNK + "/etc/javaslicer/assembly/tracer.jar";
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

	public String getJavahome() {
		return javaHome;
	}
	
	public String getJunitcore() {
		return junitCore;
	}

	public String getJavaBin() {
		return getJavahome() + "/bin";
	}
	
	public String getJunitLib() {
		return junitLib;
	}

	public static String getTrunk() {
		return getInstance().TRUNK;
	}
}
