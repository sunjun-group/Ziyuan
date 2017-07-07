/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core;

/**
 * @author LLT
 *
 */
public class JDartParams {
	private String appProperties;
	private String siteProperties;
	private String mainEntry;
	private String className;
	private String classpathStr;
	private String methodName;
	private String methodParamsStr;
	private long timeLimit; // run time limit, unit of ms
	private long minFree; // min free memory, unit of byte
	
	public JDartParams() {
		
	}

	public String getMainEntry() {
		return mainEntry;
	}

	public void setMainEntry(String mainEntry) {
		this.mainEntry = mainEntry;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClasspathStr() {
		return classpathStr;
	}

	public void setClasspathStr(String classpathStr) {
		this.classpathStr = classpathStr;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParamString() {
		return methodParamsStr;
	}

	public void setParamString(String paramString) {
		this.methodParamsStr = paramString;
	}

	public String getAppProperties() {
		return appProperties;
	}

	public void setAppProperties(String appProperties) {
		this.appProperties = appProperties;
	}

	public String getSiteProperties() {
		return siteProperties;
	}

	public void setSiteProperties(String siteProperties) {
		this.siteProperties = siteProperties;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public long getMinFree() {
		return minFree;
	}

	public void setMinFree(long minFree) {
		this.minFree = minFree;
	}
	
}
