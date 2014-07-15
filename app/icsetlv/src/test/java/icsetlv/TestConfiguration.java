/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

/**
 * @author LLT
 *
 */
public class TestConfiguration {
	private static TestConfiguration config;
	private static final String junitCore = "org.junit.runner.JUnitCore";
	private static final String TRUNK = "D:\\_1_Projects\\icsetlv\\workspace\\trunk\\";
	private String javaHome = "D:\\_1_Projects\\_Tools\\jdk1.7.0_51";
	private String sourcePath = TRUNK + "app\\icsetlv\\src\\test\\java";
	private String binPath = TRUNK + "app\\icsetlv\\target\\test-classes";
	private String junitLib = TRUNK + "app\\icsetlv\\src\\test\\lib\\*";
	private int vmDefaultPort = 8787;
	
	private TestConfiguration() {
		
	}

	public static TestConfiguration getInstance() {
		if (config == null) {
			config = new TestConfiguration();
		}
		return config;
	}

	public String getSourcepath() {
		return sourcePath;
	}

	public String getJavahome() {
		return javaHome;
	}
	
	public String getJreFolder() {
		return getJavahome() + "\\jre";
	}

	public String getJunitcore() {
		return junitCore;
	}

	public String getAppBinpath() {
		return binPath;
	}
	
	public String getJavaBin() {
		return getJavahome() + "\\bin";
	}
	
	public int getVmDefaultPort() {
		return vmDefaultPort;
	}

	public String getJunitLib() {
		return junitLib;
	}
}
