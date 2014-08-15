/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.vm;

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
	private List<String> args; 

	public VMConfiguration() {
		classpaths = new ArrayList<String>();
		debug = true;
		port = 8787;
	}
	
	public List<String> getClasspaths() {
		return classpaths;
	}

	public String getLaunchClass() {
		return launchClass;
	}

	public void setLaunchClass(String launchClass) {
		this.launchClass = launchClass;
	}

	public void addClasspath(String path) {
		classpaths.add(path);
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
		return args;
	}

	public void setProgramArgs(List<String> args) {
		this.args = args;
	}
}
