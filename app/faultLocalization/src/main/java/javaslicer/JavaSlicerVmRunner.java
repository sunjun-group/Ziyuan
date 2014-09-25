/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package javaslicer;

import icsetlv.common.utils.CollectionBuilder;
import icsetlv.vm.VMConfiguration;
import icsetlv.vm.VMRunner;

import java.util.Collection;

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
