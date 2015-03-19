/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.context;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import sav.common.core.Constants;
import sav.common.core.iface.IPrintStream;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;
import slicer.javaslicer.JavaSlicer;
import codecoverage.jacoco.agent.JaCoCoAgent;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT 
 * This application context is for application configuration
 * centralization, necessary implementation for an algorithm interface
 * will be initialized lazily upon purpose, and different parameters
 * will be required depend on what we need for selected algorithm
 * implementation.
 */
public abstract class AbstractApplicationContext implements IApplicationContext {
	private ISlicer slicer;
	private VMConfiguration vmConfig;
	protected ICodeCoverage codeCoverageTool;

	protected ICodeCoverage initCodeCoverage() {
//		return new JavaCoCo();
		return initJacocoAgent();
	}
	
	private ICodeCoverage initJacocoAgent() {
		JaCoCoAgent jacoco = new JaCoCoAgent();
		jacoco.setOut(getVmRunnerPrintStream());
		VMConfiguration config = getVmConfig();
		config.addClasspath(getAssembly(Constants.TZUYU_JACOCO_ASSEMBLY));
		jacoco.setVmConfig(config);
		return jacoco;
	}
	
	protected abstract String getAssembly(String assemblyName);

	private ISlicer initSlicer() {
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
	
	private VMConfiguration initVmConfig() {
		VMConfiguration config = new VMConfiguration();
		config.setJavaHome(getJavahome());
		config.setClasspath(getProjectClasspath());
		return config;
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
			codeCoverageTool = initCodeCoverage();
		}
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
	
	protected abstract String getJavahome();

	protected abstract String getTracerJarPath();
	
	protected abstract List<String> getProjectClasspath();

	public abstract SpectrumAlgorithm getSuspiciousnessCalculationAlgorithm();
	
	public abstract IPrintStream getVmRunnerPrintStream();
}
