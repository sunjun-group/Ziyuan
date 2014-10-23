/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.ArrayList;
import java.util.Collection;

import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.AgentVmRunner;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class JavaSlicerVmRunner extends AgentVmRunner {
	private String traceFilePath;
	
	public JavaSlicerVmRunner(String tracerJarPath) {
		super(tracerJarPath);
	}
	
	@Override
	protected void appendAgentParams(ArrayList<String> params) {
		params.add(newAgentOption("tracefile", traceFilePath));
		
	}
	
	@Override
	protected void buildProgramArgs(VMConfiguration config,
			CollectionBuilder<String, Collection<String>> builder) {
		super.buildProgramArgs(config, builder);
		builder.add("-s small pmd");
	}
	
	public void setTracerJarPath(String tracerJarPath) {
		agentJarPath = tracerJarPath;
	}

	public void setTraceFilePath(String traceFilePath) {
		this.traceFilePath = traceFilePath;
	}
}
