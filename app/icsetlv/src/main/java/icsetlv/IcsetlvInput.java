/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.vm.VMConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LLT
 *
 */
public class IcsetlvInput {
	private VMConfiguration config;
	private Map<String, List<String>> assertionSourcePaths;
	private String appOutput;
	private List<String[]> testMethods;
	private List<String> srcFolders;
	private List<String> passTestcases;
	private List<String> failTestcases;
	private List<BreakPoint> bkps;
	private int varRetrieveLevel = 4;
	
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

	public Map<String, List<String>> getAssertionSourcePaths() {
		return assertionSourcePaths;
	}

	public void setAssertionSourcePaths(Map<String, List<String>> assertionSourcePaths) {
		this.assertionSourcePaths = assertionSourcePaths;
	}

	public String getAppOutput() {
		return appOutput;
	}

	public void setAppOutput(String appOutput) {
		this.appOutput = appOutput;
		config.addClasspath(appOutput);
	}

	public List<String[]> getTestMethods() {
		return testMethods;
	}

	public void setTestMethods(List<String[]> list) {
		this.testMethods = list;
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
			config.setLaunchClass("org.junit.runner.JUnitCore");
		}
	}

	public void setBreakpoints(List<BreakPoint> bkps) {
		this.bkps = bkps;
	}
	
	public List<BreakPoint> getBreakpoints() {
		return bkps;
	}

	public int getVarRetrieveLevel() {
		return varRetrieveLevel;
	}

	public void setVarRetrieveLevel(int varRetrieveLevel) {
		this.varRetrieveLevel = varRetrieveLevel;
	}
}
