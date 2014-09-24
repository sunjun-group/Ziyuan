/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.slicer;

import java.util.List;

/**
 * @author LLT
 * 
 */
public class SlicerInput {
	private String jre;
	private String appBinFolder;
	private List<String> appSrcFolder;
	private List<String[]> classEntryPoints;

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

	public void setClassEntryPoints(List<String[]> classEntryPoints) {
		this.classEntryPoints = classEntryPoints;
	}
	
}
