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

import learntest.core.gentest.TestGenerator.GentestResult;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;

/**
 * @author LLT
 *
 */
public class JunitTestsInfo {
	private String mainClass;
	private List<String> junitClasses;
	private List<String> junitTestcases;
	
	public JunitTestsInfo(List<String> junitClasses, ClassLoader classLoader) {
		this();
		addJunitClass(junitClasses, classLoader);
	}

	public JunitTestsInfo() {
		junitClasses = new ArrayList<String>();
		junitTestcases = new ArrayList<String>();
	}
	
	public JunitTestsInfo(GentestResult testResult, ClassLoader classLoader) {
		this();
		addJunitClass(testResult, classLoader);
	}

	public void addJunitClass(GentestResult testResult, ClassLoader classLoader) {
		if (testResult.isEmpty()) {
			return;
		}
		addJunitClass(testResult.getJunitClassNames(), classLoader);
		this.mainClass = testResult.getMainClassName();
	}

	public void addJunitClass(String clazz, List<String> testMethods) {
		junitClasses.add(clazz);
		junitTestcases.addAll(testMethods);
	}

	public List<String> getJunitClasses() {
		return junitClasses;
	}

	public List<String> getJunitTestcases() {
		return junitTestcases;
	}

	public void addJunitClass(List<String> junitClasses, ClassLoader classLoader) {
		try {
			this.junitClasses.addAll(junitClasses);
			this.junitTestcases.addAll(JunitUtils.extractTestMethods(junitClasses, classLoader));
		} catch (ClassNotFoundException e) {
			throw new SavRtException(e);
		}
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
	
}
