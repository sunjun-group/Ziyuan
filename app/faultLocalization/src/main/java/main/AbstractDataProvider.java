/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import icsetlv.iface.ISlicer;
import icsetlv.vm.VMConfiguration;
import javacocoWrapper.JavaCoCo;
import javaslicer.JavaSlicer;

/**
 * @author LLT
 *
 */
public abstract class AbstractDataProvider implements IDataProvider {
	private ISlicer slicer;
	private VMConfiguration vmConfig;
	private ICodeCoverage codeCoverageTool;
	
	public AbstractDataProvider() {
		
	}
	
	protected VMConfiguration initVmConfig() {
		VMConfiguration config = new VMConfiguration();
		config.setJavaHome(getJavaHome());
		config.setClasspath(getProjectClasspath());
		return config;
	}
	
	protected ISlicer initSlicer() {
		JavaSlicer javaSlicer = new JavaSlicer();
		javaSlicer.setVmConfig(getVmConfig());
		javaSlicer.setTracerJarPath(getTracerJarPath());
		return javaSlicer;
	}
	
	public VMConfiguration getVmConfig() {
		if (vmConfig == null) {
			vmConfig = initVmConfig();
		}
		return vmConfig;
	}

	@Override
	public ISlicer getSlicer() {
		if (slicer == null) {
			slicer = initSlicer();
		}
		return slicer;
	}
	
	@Override
	public ICodeCoverage getCodeCoverageTool() {
		if (codeCoverageTool == null) {
			codeCoverageTool = new JavaCoCo();
		}
		return codeCoverageTool;
	}
	
	public int getAvailableDebuggingPort() {
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
		return -1;		
	}	
	
	/* Declare data */
	protected abstract String getJavaHome();

	protected abstract String getTracerJarPath();

	protected abstract List<String> getProjectClasspath();

}
