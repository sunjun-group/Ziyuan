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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class AppJavaClassPath {
	private String javaHome;
	private Set<String> classpaths;
	private String src;
	private String target;
	private String testTarget;
	/* user-defined variables, such as external jar need to be included for a certain executing,
	 * like additional classpath to start JunitRunner */
	private HashMap<String, String> variables = new HashMap<String, String>();

	public AppJavaClassPath() {
		classpaths = new HashSet<String>();
		variables = new HashMap<String, String>();
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
	
	public String getVariable(String variable) {
		return variables.get(variable); 
	}
	
	public String setVariable(String variable, String value) {
		return variables.put(variable, value); 
	}
}
