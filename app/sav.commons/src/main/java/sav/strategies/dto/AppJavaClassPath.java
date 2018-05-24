/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sav.common.core.SystemVariables;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class AppJavaClassPath {
	private String javaHome;
	private Set<String> classpaths;
	private String src;
	private String testSrc;
	private String target;
	private String testTarget;
	private SystemPreferences preferences;

	public AppJavaClassPath() {
		classpaths = new HashSet<String>();
		preferences = new SystemPreferences();
	}

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	public List<String> getClasspaths() {
		return new ArrayList<String>(classpaths);
	}

	public void addClasspaths(List<String> paths) {
		classpaths.addAll(paths);
	}

	public void addClasspath(String path) {
		classpaths.add(path);
	}
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
	public String getTestSrc() {
		return testSrc;
	}

	public void setTestSrc(String testSrc) {
		this.testSrc = testSrc;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTestTarget() {
		return testTarget;
	}

	public void setTestTarget(String testTarget) {
		this.testTarget = testTarget;
	}
	
	public String getClasspathStr() {
		return StringUtils.join(classpaths, File.pathSeparator);		
	}
	
	public SystemPreferences getPreferences() {
		return preferences;
	}
	
	public ClassLoader getClassLoader(ClassLoader defaultIfNull) {
		ClassLoader classLoader = getClassLoader();
		if (classLoader == null) {
			return defaultIfNull;
		}
		return classLoader;
	}
	
	public ClassLoader getClassLoader() {
		return getPreferences().get(SystemVariables.PROJECT_CLASSLOADER);
	}
	
	public void clearClasspath() {
		classpaths.clear();
	}
}
