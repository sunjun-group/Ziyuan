/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jacoco.agent.AgentJar;

import sav.common.core.utils.CollectionBuilder;
import sav.common.core.utils.StringUtils;
import sav.strategies.vm.AgentVmRunner;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class JaCoCoVmRunner extends AgentVmRunner {
	private List<String> analyzedClassNames;
	private List<String> programArgs;
	
	public JaCoCoVmRunner() throws IOException {
		super(AgentJar.extractToTempLocation().getAbsolutePath());
		AgentJar.extractToTempLocation();
		analyzedClassNames = new ArrayList<String>();
		programArgs = new ArrayList<String>();
	}
	
	@Override
	protected void appendAgentParams(ArrayList<String> params) {
		if (analyzedClassNames != null) {
			params.add(newAgentOption("includes", StringUtils.join(analyzedClassNames, ":")));
		}
	}
	
	@Override
	protected void buildProgramArgs(VMConfiguration config,
			CollectionBuilder<String, Collection<String>> builder) {
		super.buildProgramArgs(config, builder);
		for (String arg : programArgs) {
			builder.add(arg);
		}
	}
	
	public List<String> getProgramArgs() {
		return programArgs;
	}
	
	public void addProgramArg(String opt, String... values) {
		addProgramArg(opt, Arrays.asList(values));
	}
	
	public void addProgramArg(String opt, List<String> values) {
		programArgs.add("-" + opt);
		for (String value : values) {
			programArgs.add(value);
		}
		
	}
	
	public void setProgramArgs(String opt, String... values) {
		programArgs = new ArrayList<String>();
		addProgramArg(opt, values);
	}
	
	public JaCoCoVmRunner setAppend(boolean append) {
		agentParams.put("append", String.valueOf(append));
		return this;
	}
	
	public JaCoCoVmRunner setOutputMode(String outputMode) {
		agentParams.put("output", outputMode);
		return this;
	}

	public JaCoCoVmRunner setDestfile(String destfile) {
		agentParams.put("destfile", destfile);
		return this;
	}
	
	public JaCoCoVmRunner setClassdumpdir(String classdumpdir) {
		agentParams.put("classdumpdir", classdumpdir);
		return this;
	}
	
	public JaCoCoVmRunner setAnalyzedClassNames(List<String> analyzedClassNames) {
		this.analyzedClassNames = analyzedClassNames;
		return this;
	}
	
	public JaCoCoVmRunner addAnalyzedClassNames(List<String> analyzedClassNames) {
		this.analyzedClassNames.addAll(analyzedClassNames);
		return this;
	}
	
	public JaCoCoVmRunner addAnalyzedClassName(String className) {
		this.analyzedClassNames.add(className);
		return this;
	}
	
}
