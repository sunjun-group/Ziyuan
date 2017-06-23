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

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GentestResult {
	private static GentestResult EMTPY_RESULT;
	private List<String> junitClassNames;
	private List<File> junitfiles;
	private File mainClassFile;
	private String mainClassName;
	private List<BreakpointValue> inputData = new ArrayList<BreakpointValue>();
	
	public void addInputData(BreakpointValue value) {
		inputData.add(value);
	}

	public List<String> getJunitClassNames() {
		return junitClassNames;
	}

	public List<File> getJunitfiles() {
		return junitfiles;
	}
	
	public List<BreakpointValue> getTestInputs() {
		return inputData;
	}
	
	public static GentestResult getEmptyResult() {
		if (EMTPY_RESULT == null) {
			EMTPY_RESULT = new GentestResult();
		}
		return EMTPY_RESULT;
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
}
