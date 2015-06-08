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

import sav.common.core.utils.CollectionUtils;
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
	// TODO LLT: TO REMOVE
	private String tracerJarPath;
	private List<String> appClasspaths;
	private List<String> sysClasspaths;
	private String appSrc;
	private String appTestTarget;
	private String tzuyuJacocoAssembly;
	private String appTarget;
	

	public SpectrumAlgorithm getSuspiciousCalculAlgo() {
		return suspiciousCalculAlgo;
	}

	public void setSuspiciousCalculAlgo(
			SpectrumAlgorithm suspiciousCalculAlgo) {
		this.suspiciousCalculAlgo = suspiciousCalculAlgo;
	}
	
	public VMConfiguration getVmConfig() {
		if (vmConfig == null) {
			vmConfig = new VMConfiguration();
			vmConfig.setClasspath(appClasspaths);
			vmConfig.setJavaHome(getJavahome());
		}
		/**
		 * we init a new one to make sure the configuration is not dirty with
		 * some internal properties.
		 */
		return new VMConfiguration(vmConfig);
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

	public List<String> getAppClasspaths() {
		return appClasspaths;
	}

	public void setClasspaths(List<String> classpaths) {
		this.appClasspaths = classpaths;
	}
	
	public void addClasspath(String classpath) {
		appClasspaths = CollectionUtils.nullToEmpty(appClasspaths);
		appClasspaths.add(classpath);
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

	public String getAppSrc() {
		return appSrc;
	}

	public void setAppSrc(String appSrc) {
		this.appSrc = appSrc;
	}

	public String getAppTarget() {
		return appTarget;
	}

	public void setAppTarget(String appTarget) {
		this.appTarget = appTarget;
	}
	
	public String getAppTestTarget() {
		return appTestTarget;
	}

	public void setAppTestTarget(String appTestTarget) {
		this.appTestTarget = appTestTarget;
	}

	public String getAppClasspathStr() {
		return StringUtils.join(appClasspaths, File.pathSeparator);
	}
	
	public String getTzuyuJacocoAssembly() {
		return tzuyuJacocoAssembly;
	}
	
	public void setTzuyuJacocoAssembly(String tzuyuJacocoAssembly) {
		this.tzuyuJacocoAssembly = tzuyuJacocoAssembly;
	}
}