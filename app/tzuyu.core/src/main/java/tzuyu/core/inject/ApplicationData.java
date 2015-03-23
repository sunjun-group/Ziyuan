/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.inject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import sav.common.core.utils.StringUtils;
import sav.strategies.vm.VMConfiguration;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public class ApplicationData {
	private SpectrumAlgorithm suspiciousCalculAlgo;
	private String javaHome;
	private VMConfiguration vmConfig;
	private String tracerJarPath;
	private List<String> appClasspaths;
	private List<String> sysClasspaths;
	private String scrFolder;
	

	public SpectrumAlgorithm getSuspiciousCalculAlgo() {
		return suspiciousCalculAlgo;
	}

	public void setSuspiciousCalculAlgo(
			SpectrumAlgorithm suspiciousCalculAlgo) {
		this.suspiciousCalculAlgo = suspiciousCalculAlgo;
	}
	
	public VMConfiguration getVmConfig() {
		if (vmConfig == null) {
			vmConfig.setClasspath(appClasspaths);
			vmConfig.setJavaHome(getJavahome());
		}
		return vmConfig;
	}
	
	protected String getJavahome() {
		return javaHome;
	}

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	public String getTracerJarPath() {
		return tracerJarPath;
	}

	public void setTracerJarPath(String tracerJarPath) {
		this.tracerJarPath = tracerJarPath;
	}

	public List<String> getClasspaths() {
		return appClasspaths;
	}

	public void setClasspaths(List<String> classpaths) {
		this.appClasspaths = classpaths;
	}

	/**
	 * return the classpath at runtime
	 */
	public List<String> getSysClasspaths() {
		if (sysClasspaths == null) {
			sysClasspaths = Arrays.asList(System.getProperty("java.class.path")
					.split(";"));
		}
		return sysClasspaths;
	}

	public String getScrFolder() {
		return scrFolder;
	}

	public void setScrFolder(String scrFolder) {
		this.scrFolder = scrFolder;
	}

	public String getAppClasspathStr() {
		return StringUtils.join(appClasspaths, File.pathSeparator);
	}
}