/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.AgentVmRunner;
import sav.strategies.vm.VMConfiguration;
import de.unisb.cs.st.javaslicer.tracer.Tracer;

/**
 * @author LLT
 *
 */
public class JavaSlicerVmRunner extends AgentVmRunner {
	private static final String AGENT_PARAM_SEPARATOR = ":";
	private String traceFilePath;
	
	public JavaSlicerVmRunner() {
		super(getTracerJarPath());
	}
	
	@Override
	protected void appendAgentParams(ArrayList<String> params) {
		params.add(newAgentOption("tracefile", traceFilePath));
	}
	
	@Override
	protected void buildVmOption(CollectionBuilder<String, ?> builder,
			VMConfiguration config) {
		/* disable jdk verifier, no checking version */
		builder.add("-noverify");
		super.buildVmOption(builder, config);
	}
	
	@Override
	protected void buildProgramArgs(VMConfiguration config,
			CollectionBuilder<String, Collection<String>> builder) {
		super.buildProgramArgs(config, builder);
	}
	
	public void setTraceFilePath(String traceFilePath) {
		this.traceFilePath = traceFilePath;
	}
	
	@Override
	protected String getAgentOptionSeparator() {
		return AGENT_PARAM_SEPARATOR;
	}
	
	public static String getTracerJarPath() {
		try {
			String path = Tracer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			path = URLDecoder.decode(path, "UTF-8");
			File newFile = sav.common.core.utils.FileUtils.getFileInTempFolder("tracer.jar");
			if (!newFile.exists()) {
				FileUtils.copyFile(new File(path), newFile);
			}
			return newFile.getAbsolutePath();
		} catch (Exception e) {
			throw new SavRtException("cannot get path of tracer.jar", e);
		}
	}
}
