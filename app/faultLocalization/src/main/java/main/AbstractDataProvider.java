/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import icsetlv.iface.ISlicer;
import icsetlv.vm.VMConfiguration;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import javacocoWrapper.JavaCoCo;
import javaslicer.JavaSlicer;
import sav.common.core.utils.ConfigUtils;

/**
 * @author LLT
 * 
 */
public abstract class AbstractDataProvider implements IDataProvider {
	private ISlicer slicer;
	private VMConfiguration vmConfig;
	private ICodeCoverage codeCoverageTool;

	public AbstractDataProvider() {
		vmConfig = initVmConfig();
		slicer = initSlicer();
		codeCoverageTool = new JavaCoCo();
	}

	private VMConfiguration initVmConfig() {
		VMConfiguration config = new VMConfiguration();
		config.setJavaHome(ConfigUtils.getJavaHome());
		config.setClasspath(getProjectClasspath());
		return config;
	}

	private ISlicer initSlicer() {
		JavaSlicer javaSlicer = new JavaSlicer();
		javaSlicer.setVmConfig(vmConfig);
		javaSlicer.setTracerJarPath(getTracerJarPath());
		return javaSlicer;
	}

	@Override
	public ISlicer getSlicer() {
		return slicer;
	}

	@Override
	public ICodeCoverage getCodeCoverageTool() {
		return codeCoverageTool;
	}

	public int getAvailableDebuggingPort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
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

	protected String getTracerJarPath() {
		return ConfigUtils.getTracerLibPath();
	}

	protected abstract List<String> getProjectClasspath();

}
