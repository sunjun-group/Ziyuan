/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.wala;

import java.io.File;
import java.util.List;

/**
 * @author LLT
 * 
 */
public class SlicerInput {
	private static final String JRE_FOLDER = "jre";
	private String jre;
	private String appBinFolder;
	private List<String> appSrcFolder;
	private List<String[]> classEntryPoints;

	public String getJavaHome() {
		return jre;
	}

	public void setJre(String javaHome) {
		// TODO Check documentation here:
		// http://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
		// In short: java.home system variable already points to jre folder
		if (javaHome.contains(JRE_FOLDER)) {
			this.jre = javaHome;
		} else {			
			this.jre = javaHome + File.separator + "jre";
		}
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

	public void setClassEntryPoints(List<String[]> classEntryPoints) {
		this.classEntryPoints = classEntryPoints;
	}
	
}
