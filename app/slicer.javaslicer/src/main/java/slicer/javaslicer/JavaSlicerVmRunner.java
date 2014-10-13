/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.Collection;

import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class JavaSlicerVmRunner extends VMRunner {
	private String tracerJarPath;
	private String traceFilePath;
	
	public JavaSlicerVmRunner(String tracerJarPath) {
		this.tracerJarPath = tracerJarPath;
	}
	
	@Override
	protected void buildVmOption(CollectionBuilder<String, ?> builder,
			VMConfiguration config) {
		builder.add(String.format("-javaagent:%s=tracefile:%s", tracerJarPath,
				traceFilePath));
	}
	
	@Override
	protected void buildProgramArgs(VMConfiguration config,
			CollectionBuilder<String, Collection<String>> builder) {
		super.buildProgramArgs(config, builder);
		builder.add("-s small pmd");
	}
	
	public void setTracerJarPath(String tracerJarPath) {
		this.tracerJarPath = tracerJarPath;
	}

	public void setTraceFilePath(String traceFilePath) {
		this.traceFilePath = traceFilePath;
	}
}
