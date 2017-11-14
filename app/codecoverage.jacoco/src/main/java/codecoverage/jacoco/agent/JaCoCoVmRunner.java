/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jacoco.agent.AgentJar;
import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.AgentOptions.OutputMode;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.vm.AgentVmRunner;

/**
 * @author LLT
 *
 */
public class JaCoCoVmRunner extends AgentVmRunner {
	private static String agentJarPath;
	private Collection<String> analyzedClassNames;
	
	public JaCoCoVmRunner() throws IOException {
		super(getAgentJar());
		analyzedClassNames = new ArrayList<String>();
	}

	private static String getAgentJar() throws IOException {
		boolean needToExtract = false;
		if (agentJarPath == null) {
			needToExtract = true;
		} else {
			File agentJar = new File(agentJarPath);
			if (!agentJar.exists()) {
				needToExtract = true;
			}
		}
		if (needToExtract) {
			agentJarPath = AgentJar.extractToTempLocation().getAbsolutePath();
		}
		return agentJarPath;
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
	
	public JaCoCoVmRunner setOutputMode(OutputMode outputMode) {
		addAgentParam(AgentOptions.OUTPUT, outputMode.name());
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

	public JaCoCoVmRunner setAnalyzedClassNames(Collection<String> analyzedClassNames) {
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
