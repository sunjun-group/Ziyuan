/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.classinfo;

import java.util.ArrayList;
import java.util.List;

import learntest.core.gentest.GentestResult;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.JunitUtils;

/**
 * @author LLT
 *
 */
public class JunitTestsInfo {
	private String mainClass;
	private List<String> junitClasses;
	private List<String> junitTestcases;
	private List<String> junitFiles;
	
	public JunitTestsInfo(List<String> junitClasses, ClassLoader classLoader) {
		this();
		addJunitClass(junitClasses, classLoader);
	}

	public JunitTestsInfo() {
		junitClasses = new ArrayList<String>();
		junitTestcases = new ArrayList<String>();
		junitFiles = new ArrayList<String>(); 
	}
	
	public JunitTestsInfo(GentestResult testResult, ClassLoader classLoader) {
		this();
		addJunitClass(testResult, classLoader);
	}

	public void addJunitClass(GentestResult testResult, ClassLoader classLoader) {
		if (testResult.isEmpty()) {
			return;
		}
		CollectionUtils.addIfNotNullNotExist(junitFiles, FileUtils.getFilePaths(testResult.getJunitfiles()));
		addJunitClass(testResult.getJunitClassNames(), classLoader);
		this.mainClass = testResult.getMainClassName();
	}

	public void addJunitClass(String clazz, List<String> testMethods) {
		CollectionUtils.addIfNotNullNotExist(this.junitClasses, clazz);
		CollectionUtils.addIfNotNullNotExist(this.junitTestcases, testMethods);
	}

	public List<String> getJunitClasses() {
		return junitClasses;
	}

	public List<String> getJunitTestcases() {
		return junitTestcases;
	}

	private void addJunitClass(List<String> junitClasses, ClassLoader classLoader) {
		CollectionUtils.addIfNotNullNotExist(this.junitClasses, junitClasses);
		CollectionUtils.addIfNotNullNotExist(this.junitTestcases, JunitUtils.extractTestMethods(junitClasses, classLoader));
	}
	
	public void addJunitClass(String junitClasses, ClassLoader classLoader) {
		addJunitClass(CollectionUtils.listOf(junitClasses, 1), classLoader);
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	public List<String> getJunitFiles() {
		return junitFiles;
	}
}
