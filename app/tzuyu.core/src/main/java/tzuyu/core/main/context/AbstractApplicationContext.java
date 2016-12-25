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
import java.util.HashMap;
import java.util.List;

import mutation.mutator.Mutator;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.slicing.ISlicer;
import slicer.javaslicer.JavaSlicer;
import codecoverage.jacoco.agent.JaCoCo;

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
	protected ICodeCoverage codeCoverageTool;
	private IMutator mutator;
	private AppJavaClassPath appClassPath;

	protected ICodeCoverage initCodeCoverage() {
		return initJacocoAgent();
	}
	
	private ICodeCoverage initJacocoAgent() {
		JaCoCo jacoco = new JaCoCo(getAppData());
		return jacoco;
	}
	
	private ISlicer initSlicer() {
		JavaSlicer javaSlicer = new JavaSlicer();
		return javaSlicer;
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

	public IMutator getMutator() {
		if (this.mutator == null) {
			Mutator mutator = new Mutator(getAppData().getSrc());
			mutator.setOpMapConfig(new HashMap<String, List<String>>());
			this.mutator = mutator;
			
		}
		return this.mutator;
	}

	public AppJavaClassPath getAppData() {
		return appClassPath;
	}

	public void setAppData(AppJavaClassPath appClassPath) {
		this.appClassPath = appClassPath;
	}
}
