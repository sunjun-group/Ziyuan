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
	private int explore_node = -1;
	private int explore_branch = -1;
	
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
	
	public static JDartParams constructOnDemandJDartParams(String classpathStr, String mainEntry, String className, 
			String methodName, String paramString,
			String app, String site, int node, int branch) {
		
		long minFree = 20*(1024<<10); // min free memory
		long timeLimit = 30 * 1000;
		
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
		params.setExploreNode(node);
		params.setExploreBranch(branch);

		return params;
	}
	
	public static JDartParams defaultOnDemandJDartParams() {
		String  classpathStr = "E:\\workspace\\JPF\\data/apache-common-math-2.2/bin", 
				app = "libs/jdart/jpf.properties",
				site = "libs/jpf_on_demand.properties",
				
				mainEntry = "com.Example",
				className = "com.Example",
				methodName = "foo",
				paramString = "(x:int,y:int)";
		
		int node = 5; // cfg node index
		int branch = 0; // 0,1 , missing branch
		
		return constructOnDemandJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site, node, branch);
	}
	
	public static JDartParams defaultJDartParams() {
		String  classpathStr = "E:\\workspace\\JPF\\data/apache-common-math-2.2/bin", 
				app = "libs/jdart/jpf.properties",
				site = "libs/jpf.properties", //if only want to solve once, change to libs/jpf_once.properties
				
				mainEntry = "com.Test",
				className = "org.apache.commons.math.util.FastMath",
				methodName = "floor",
				paramString = "(x:double)";
//				mainEntry = "testdata.l2t.init.mersennetwister.next.MersenneTwisterMain",
//				className = "org.apache.commons.math.random.MersenneTwister",
//				methodName = "next",
//				paramString = "(bits:int)";

		/** return one results */
//		mainEntry = "testdata.l2t.init.continuousoutputmodel.setinterpolatedtime.ContinuousOutputModelMain";
//		className = "org.apache.commons.math.ode.ContinuousOutputModel";
//		methodName = "setInterpolatedTime";
//		paramString = "(time:double)";
//		
//		/** return two results */
//		mainEntry = "testdata.l2t.init.mersennetwister.next.MersenneTwisterMain";
//		className = "org.apache.commons.math.random.MersenneTwister";
//		methodName = "next";
//		paramString = "(bits:int)";//line 224
		
		/** classcast exception */
//		mainEntry = "testdata.l2t.init.mersennetwister.setseed.MersenneTwisterMain";
//		className = "org.apache.commons.math.random.MersenneTwister";
//		methodName = "setSeed";
//		paramString = "(seed:int[])";
		mainEntry = "com.Example";
		className = "com.Example";
		methodName = "foo";
		paramString = "(x:int,y:int)";
		
		return constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site);
	}
	
	public static JDartParams constructJDartParams(String classpathStr, String mainEntry, String className, String methodName, String paramString,
			String app, String site) {
		
		long minFree = 20*(1024<<10); // min free memory
		long timeLimit = 300000 * 1000;
		
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
}
