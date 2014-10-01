/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package commons;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class TestConfiguration {
	public static final String ICSETLV = "icsetlv";
	public static final String FALTLOCALISATION = "faultLocalization";
	private static final String TZUYU_HOME = "TZUYU_HOME";

	private static TestConfiguration config;
	private static final String junitCore = "org.junit.runner.JUnitCore";
	public String javaHome = System.getProperty("java.home");
	public String TRUNK;
	public String junitLib;
	public String tracerLibPath;
	public String javaSlicerPath;

	private TestConfiguration() {
		TRUNK = getTrunkPath();
		junitLib = TRUNK + "/app/icsetlv/src/test/lib/*";
		tracerLibPath = TRUNK + "/etc/javaslicer/assembly/tracer.jar";
	}

	/**
	 * Try to get the trunk path from either:
	 * <ol>
	 * <li>System variable 'TZUYU_HOME'</li>
	 * <li>Environment variable 'TZUYU_HOME'</li>
	 * <li>Resource bundle 'test_configuration.properties'</li>
	 * </ol>
	 * If the value is defined in multiple locations, the first one is
	 * preferred. If no value was defined in all three locations,
	 * {@link MissingResourceException} is thrown.
	 * 
	 * @return path to trunk if defined in one of the three locations.
	 */
	private String getTrunkPath() {
		String trunk = System.getProperty(TZUYU_HOME);
		if (org.apache.commons.lang.StringUtils.isBlank(trunk)) {
			trunk = System.getenv(TZUYU_HOME);
			if (org.apache.commons.lang.StringUtils.isBlank(trunk)) {
				trunk = ResourceBundle.getBundle("test_configuration").getString("trunk");
			}
		}
		return trunk;
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
