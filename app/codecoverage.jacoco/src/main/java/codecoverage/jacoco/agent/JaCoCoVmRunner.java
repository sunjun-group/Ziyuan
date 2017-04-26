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
import java.util.List;

import org.jacoco.agent.AgentJar;
import org.jacoco.core.runtime.AgentOptions;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.vm.AgentVmRunner;

/**
 * @author LLT
 *
 */
public class JaCoCoVmRunner extends AgentVmRunner {
	private List<String> analyzedClassNames;
	
	public JaCoCoVmRunner() throws IOException {
		super(AgentJar.extractToTempLocation().getAbsolutePath());
		analyzedClassNames = new ArrayList<String>();
	}
	
	@Override
	protected void appendAgentParams(ArrayList<String> params) {
		if (CollectionUtils.isNotEmpty(analyzedClassNames)) {
			params.add(newAgentOption("includes", StringUtils.join(analyzedClassNames, ":")));
		} 
	}
	
	public JaCoCoVmRunner setAppend(boolean append) {
		addAgentParam(AgentOptions.APPEND, String.valueOf(append));
		return this;
	}
	
	public JaCoCoVmRunner setOutputMode(String outputMode) {
		addAgentParam(AgentOptions.OUTPUT, outputMode);
		return this;
	}

	public JaCoCoVmRunner setDestfile(String destfile) {
		addAgentParam(AgentOptions.DESTFILE, destfile);
		return this;
	}
	
	public JaCoCoVmRunner setClassdumpdir(String classdumpdir) {
		addAgentParam(AgentOptions.CLASSDUMPDIR, classdumpdir);
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
