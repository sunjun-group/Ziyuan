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
	private List<String> testcasesSourcePaths;
	private String appOutput;
	private List<Pair<String, String>> testMethods;
	private List<String> srcFolders;
	private List<String> passTestcases;
	private List<String> failTestcases;
	
	public IcsetlvInput() {
		config = new VMConfiguration();
		srcFolders = new ArrayList<String>();
	}

	public VMConfiguration getConfig() {
		return config;
	}

	public void setConfig(VMConfiguration config) {
		this.config = config;
	}

	public List<String> getTestcasesSourcePaths() {
		return testcasesSourcePaths;
	}

	public void setTestcasesSourcePaths(List<String> testcasesSourcePaths) {
		this.testcasesSourcePaths = testcasesSourcePaths;
	}

	public String getAppOutput() {
		return appOutput;
	}

	public void setAppOutput(String appOutput) {
		this.appOutput = appOutput;
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
}
