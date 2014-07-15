/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.vm;

import icsetlv.common.Constants;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class VMConfiguration {
	private String javaHome;
	private List<String> classpath;
	private String clazzName;
	private boolean debug;
	private int port;
	private List<String> args; // for internal use only

	public VMConfiguration() {
		classpath = new ArrayList<String>();
		debug = true;
		port = 8787;
	}

	public String getPath() {
		return StringUtils.join(Constants.FILE_SEPARATOR, javaHome, "bin", "java");
	}

	public String getClasspaths() {
		return StringUtils.join(classpath, ";");
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public void addClasspath(String path) {
		classpath.add(path);
	}
	
	public void setClasspath(List<String> classpath) {
		this.classpath = classpath;
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

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}
}
