/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gentest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gentest.core.data.Sequence;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GentestResult {
	private List<String> junitClassNames;
	private List<File> junitfiles;
	private File mainClassFile;
	private String mainClassName;
	private List<BreakpointValue> inputData;
	private Map<String, Sequence> testcaseSequenceMap;
	
	public void addInputData(BreakpointValue value) {
		if (inputData == null) {
			inputData = new ArrayList<BreakpointValue>();
		}
		inputData.add(value);
	}

	public List<String> getJunitClassNames() {
		return junitClassNames;
	}

	public List<File> getJunitfiles() {
		return junitfiles;
	}
	
	public List<BreakpointValue> getTestInputs() {
		return CollectionUtils.nullToEmpty(inputData);
	}
	
	public static GentestResult getEmptyResult() {
		return new GentestResult();
	}

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(junitClassNames);
	}
	
	public void setJunitClassNames(List<String> junitClassNames) {
		this.junitClassNames = junitClassNames;
	}

	public void setJunitfiles(List<File> junitfiles) {
		this.junitfiles = junitfiles;
	}

	public void setInputData(List<BreakpointValue> inputData) {
		this.inputData = inputData;
	}
	
	public List<File> getAllFiles() {
		if (mainClassFile == null) {
			return getJunitfiles();
		}
		List<File> allfiles = new ArrayList<File>(junitfiles);
		allfiles.add(mainClassFile);
		return allfiles;
	}
	
	public String getMainClassNames() {
		return mainClassName;
	}

	public File getMainClassFile() {
		return mainClassFile;
	}

	public void setMainClassFile(File mainClassFile) {
		this.mainClassFile = mainClassFile;
	}

	public String getMainClassName() {
		return mainClassName;
	}

	public void setMainClassName(String mainClassName) {
		this.mainClassName = mainClassName;
	}

	public Map<String, Sequence> getTestcaseSequenceMap() {
		return testcaseSequenceMap;
	}

	public void setTestcaseSequenceMap(Map<String, Sequence> testcaseSequenceMap) {
		this.testcaseSequenceMap = testcaseSequenceMap;
	}
	
}
