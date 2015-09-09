/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.inject;

import java.util.Arrays;
import java.util.List;

import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.VMConfiguration;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public class ApplicationData {
	private SpectrumAlgorithm suspiciousCalculAlgo;
	private List<String> sysClasspaths;
	private String tzuyuJacocoAssembly;
	private AppJavaClassPath appClassPath;
	
	public ApplicationData() {
		appClassPath = new AppJavaClassPath();
	}

	public SpectrumAlgorithm getSuspiciousCalculAlgo() {
		return suspiciousCalculAlgo;
	}

	public void setSuspiciousCalculAlgo(
			SpectrumAlgorithm suspiciousCalculAlgo) {
		this.suspiciousCalculAlgo = suspiciousCalculAlgo;
	}
	
	public VMConfiguration initVmConfig() {
		return new VMConfiguration(appClassPath);
	}
	
	public String getJavaHome() {
		return appClassPath.getJavaHome();
	}

	public void setJavaHome(String javaHome) {
		appClassPath.setJavaHome(javaHome);
	}

	public List<String> getAppClasspaths() {
		return appClassPath.getClasspaths();
	}

	public void setClasspaths(List<String> classpaths) {
		appClassPath.addClasspaths(classpaths);
	}
	
	public void addClasspath(String classpath) {
		appClassPath.addClasspath(classpath);
	}

	public void addClasspaths(List<String> classPaths) {
		appClassPath.addClasspaths(classPaths);
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
		return appClassPath.getSrc();
	}

	public void setAppSrc(String appSrc) {
		appClassPath.setSrc(appSrc);
	}

	public String getAppTarget() {
		return appClassPath.getTarget();
	}

	public void setAppTarget(String appTarget) {
		appClassPath.setTarget(appTarget);
	}
	
	public String getAppTestTarget() {
		return appClassPath.getTestTarget();
	}

	public void setAppTestTarget(String appTestTarget) {
		appClassPath.setTestTarget(appTestTarget);
	}

	public String getAppClasspathStr() {
		return appClassPath.getClasspathStr();
	}
	
	public AppJavaClassPath getAppClassPath() {
		return appClassPath;
	}
	
	public String getTzuyuJacocoAssembly() {
		return tzuyuJacocoAssembly;
	}
	
	public void setTzuyuJacocoAssembly(String tzuyuJacocoAssembly) {
		this.tzuyuJacocoAssembly = tzuyuJacocoAssembly;
	}

}