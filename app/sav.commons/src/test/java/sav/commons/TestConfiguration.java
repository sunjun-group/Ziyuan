/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons;


import sav.common.core.utils.StringUtils;
import sav.commons.utils.TestConfigUtils;

/**
 * @author LLT
 * 
 */
public class TestConfiguration {
	private static TestConfiguration config = new TestConfiguration();
	public static final String JUNIT_CORE = "org.junit.runner.JUnitCore";
	public static final String TRUNK;
	public static final String APP;
	public static final String ETC;
	public static String JUNIT_LIB;
	public static String SAV_COMMONS_TEST_TARGET;
	public String javaSlicerPath;
	// do not remove this one, this is not the current java home of the
	// application
	public static String JAVA_HOME;
	public static String ASSEMBLY;
	
	static {
		TRUNK = specifyTrunk();
		ETC = TRUNK + "/etc/";
		APP = TRUNK + "/app/";
		JUNIT_LIB = TRUNK + "/app/icsetlv/src/test/lib/*";
		SAV_COMMONS_TEST_TARGET = getTestTarget("sav.commons");
		JAVA_HOME = TestConfigUtils.getJavaHome();
	}

	private static String specifyTrunk() {
		String path = TestConfiguration.class.getResource(
				TestConfiguration.class.getSimpleName() + ".class").getPath();
		return path.substring(0, path.indexOf("/app"));
	}
	
	public static String getTzAssembly(String assemblyName) {
		return StringUtils.join("", ETC, "app_assembly/", assemblyName); 
	}

	private TestConfiguration() {
	}
	
	public String getTestScrPath(String module) {
		return StringUtils.join("", APP, module, "/src/test/java");
	}

	public static String getTestTarget(String module) {
		return StringUtils.join("", APP, module, "/target/test-classes");
	}

	public static String getTarget(String module) {
		return StringUtils.join("", APP, module, "/target/classes");
	}
	
	public static String getTestResources(String module) {
		return StringUtils.join("", APP, module, "/src/test/resources");
	}

	public static TestConfiguration getInstance() {
		return config;
	}

	public String getJunitcore() {
		return JUNIT_CORE;
	}

	public String getJavaBin() {
		return JAVA_HOME + "/bin";
	}

	public String getJunitLib() {
		return JUNIT_LIB;
	}
	
}
