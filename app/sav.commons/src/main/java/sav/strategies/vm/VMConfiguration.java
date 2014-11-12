/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 * 
 */
public class VMConfiguration {
	private String javaHome;
	private List<String> classpaths;
	private String launchClass;
	private boolean debug;
	private int port;
	// for internal use only
	private List<String> programArgs; 
	private boolean enableAssertion;

	public VMConfiguration() {
		classpaths = new ArrayList<String>();
		debug = true;
		port = 8787;
		enableAssertion = true;
	}
	
	public List<String> getClasspaths() {
		return classpaths;
	}

	public String getLaunchClass() {
		return launchClass;
	}

	public VMConfiguration setLaunchClass(String launchClass) {
		this.launchClass = launchClass;
		return this;
	}

	public void addClasspath(String path) {
		classpaths.add(path);
	}
	
	public void addClasspaths(List<String> paths) {
		classpaths.addAll(paths);
	}
	
	public void setClasspath(List<String> classpath) {
		this.classpaths = classpath;
	}

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<String> getProgramArgs() {
		if (programArgs == null) {
			programArgs = new ArrayList<String>();
		}
		return programArgs;
	}

	public void setProgramArgs(List<String> programArgs) {
		this.programArgs = programArgs;
	}
	
	public VMConfiguration addProgramArgs(String newArg) {
		getProgramArgs().add(newArg);
		return this;
	}

	public boolean isEnableAssertion() {
		return enableAssertion;
	}

	public void setEnableAssertion(boolean enableAssertion) {
		this.enableAssertion = enableAssertion;
	}
	
}
