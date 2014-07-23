/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wala.util.collections.Pair;

import icsetlv.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class IcsetlvInput {
	private VMConfiguration config;
	private List<String> assertionSourcePaths;
	private String appOutput;
	private List<Pair<String, String>> testMethods;
	private List<String> srcFolders;
	private List<String> passTestcases;
	private List<String> failTestcases;
	
	public IcsetlvInput() {
		config = new VMConfiguration();
		config.setDebug(true);
		srcFolders = new ArrayList<String>();
	}

	public VMConfiguration getConfig() {
		return config;
	}

	public void setConfig(VMConfiguration config) {
		this.config = config;
	}

	public List<String> getAssertionSourcePaths() {
		return assertionSourcePaths;
	}

	public void setAssertionSourcePaths(List<String> assertionSourcePaths) {
		this.assertionSourcePaths = assertionSourcePaths;
	}

	public String getAppOutput() {
		return appOutput;
	}

	public void setAppOutput(String appOutput) {
		this.appOutput = appOutput;
		config.addClasspath(appOutput);
	}

	public List<Pair<String, String>> getTestMethods() {
		return testMethods;
	}

	public void setTestMethods(List<Pair<String, String>> testMethods) {
		this.testMethods = testMethods;
	}
	
	public void addSrcFolder(String path) {
		srcFolders.add(path);
	}
	
	public List<String> getSrcFolders() {
		return srcFolders;
	}

	public List<String> getPassTestcases() {
		return passTestcases;
	}

	public void setPassTestcases(List<String> passTestcases) {
		this.passTestcases = passTestcases;
	}

	public List<String> getFailTestcases() {
		return failTestcases;
	}

	public void setFailTestcases(List<String> failTestcases) {
		this.failTestcases = failTestcases;
	}

	public void setJavaHome(String javaHome) {
		config.setJavaHome(javaHome);
	}

	public void setJvmPort(int port) {
		config.setPort(port);
	}

	public void setPrjClasspath(List<String> prjClasspaths) {
		config.setClasspath(prjClasspaths);
	}

	public void setRunJunit(boolean cond) {
		if (cond) {
			config.setClazzName("org.junit.runner.JUnitCore");
		}
	}
}
