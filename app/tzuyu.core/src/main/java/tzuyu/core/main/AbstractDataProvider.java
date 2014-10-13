/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import sav.commons.utils.ConfigUtils;
import sav.strategies.IDataProvider;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;
import slicer.javaslicer.JavaSlicer;

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
		codeCoverageTool = initCodeCoverage();
	}

	private ICodeCoverage initCodeCoverage() {
		// TODO Auto-generated method stub
		return null;
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

	protected abstract String getTracerJarPath();
	
	protected abstract List<String> getProjectClasspath();

}
