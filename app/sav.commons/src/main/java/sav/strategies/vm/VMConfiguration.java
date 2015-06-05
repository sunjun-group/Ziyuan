/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class VMConfiguration {
	public static final int INVALID_PORT = -1;
	private String javaHome;
	private List<String> classpaths;
	private String launchClass;
	private boolean debug;
	private int port;
	private boolean enableAssertion;
	// for internal use only
	private List<String> programArgs; 
	
	public VMConfiguration(VMConfiguration config) {
		this.javaHome = config.getJavaHome();
		this.classpaths = config.getClasspaths();
		this.debug = config.isDebug();
		this.port = config.getPort();
		this.enableAssertion = config.isEnableAssertion();
	}

	public VMConfiguration() {
		classpaths = new ArrayList<String>();
		debug = true;
		port = INVALID_PORT;
		enableAssertion = true;
	}
	
	public List<String> getClasspaths() {
		return classpaths;
	}

	public String getLaunchClass() {
		return launchClass;
	}

	/**
	 * this property is only set when we start running jvm, not suppose to be
	 * set in advance.
	 */
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
		if (port == INVALID_PORT) {
			port = findFreePort();
		}
		return port;
	}
	
	public static int findFreePort() {
		ServerSocket socket= null;
		try {
			socket= new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) { 
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return INVALID_PORT;		
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

	public String getClasspathStr() {
		return StringUtils.join(classpaths, File.pathSeparator);
	}

}
