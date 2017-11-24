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
	private String onDemandSiteProperties;
	private String mainEntry;
	private String className;
	private String classpathStr;
	private String methodName;
	private String methodParamsStr;
	private long timeLimit; // run time limit, unit of ms
	private long minFree; // min free memory, unit of byte
	private int explore_node = -1;
	private int explore_branch = -1;
	private int limitNumberOfResultSet = 500; // default, copy from JDartClient
												// class.

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

	public int getExploreNode() {
		return explore_node;
	}

	public void setExploreNode(int explore_node) {
		this.explore_node = explore_node;
	}

	public int getExploreBranch() {
		return explore_branch;
	}

	public void setExploreBranch(int explore_branch) {
		this.explore_branch = explore_branch;
	}

	public int getLimitNumberOfResultSet() {
		return limitNumberOfResultSet;
	}

	public void setLimitNumberOfResultSet(int limitNumberOfResultSet) {
		this.limitNumberOfResultSet = limitNumberOfResultSet;
	}

	public String getOnDemandSiteProperties() {
		return onDemandSiteProperties;
	}

	public void setOnDemandSiteProperties(String onDemandSiteProperties) {
		this.onDemandSiteProperties = onDemandSiteProperties;
	}

	public static JDartParams constructJDartParams(String classpathStr, String mainEntry, String className,
			String methodName, String paramString, String app, String site) {
		long minFree = (1024 << 10); // min free memory
		long timeLimit = 60 * 1000; // ms
		JDartParams params = new JDartParams();
		params.setAppProperties(app);
		params.setClassName(className);
		params.setClasspathStr(classpathStr);
		params.setMainEntry(mainEntry);
		params.setMethodName(methodName);
		params.setMinFree(minFree);
		params.setParamString(paramString);
		params.setSiteProperties(site);
		params.setTimeLimit(timeLimit);

		return params;
	}

	public static JDartParams constructOnDemandJDartParams(String classpathStr, String mainEntry, String className,
			String methodName, String paramString, String app, String site, String onDemandSite, int node, int branch) {
		JDartParams params = constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site);
		params.setOnDemandSiteProperties(onDemandSite);
		params.setExploreNode(node);
		params.setExploreBranch(branch);
		return params;
	}
}
