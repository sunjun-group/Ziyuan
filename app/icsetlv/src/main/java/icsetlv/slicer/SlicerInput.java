/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.slicer;

import icsetlv.common.dto.BreakPoint;

import java.util.List;

/**
 * @author LLT
 * 
 */
public class SlicerInput {
	private ClassLoader classLoader;
	private String jre;
	private String appBinFolder;
	private List<String> appSrcFolder;
	// void methods only.
	private List<String[]> classEntryPoints;
	private List<BreakPoint> breakpoints;

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public String getJavaHome() {
		return jre;
	}

	public void setJre(String javaHome) {
		this.jre = javaHome + "\\jre";
	}

	public String getAppBinFolder() {
		return appBinFolder;
	}

	public void setAppBinFolder(String appClassPath) {
		this.appBinFolder = appClassPath;
	}
	
	public List<String> getAppSrcFolder() {
		return appSrcFolder;
	}

	public void setAppSrcFolder(List<String> appSrcFolder) {
		this.appSrcFolder = appSrcFolder;
	}

	public List<String[]> getClassEntryPoints() {
		return classEntryPoints;
	}

	public void setClassEntryPoints(List<String[]> entrypoints) {
		this.classEntryPoints = entrypoints;
	}

	public List<BreakPoint> getBreakpoints() {
		return breakpoints;
	}

	public void setBreakpoints(List<BreakPoint> breakpoints) {
		this.breakpoints = breakpoints;
	}
}
