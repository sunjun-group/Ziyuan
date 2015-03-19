/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;


import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavPrintStream;
import sav.common.core.iface.IPrintStream;
import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;
import tzuyu.core.main.context.AbstractApplicationContext;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;


/**
 * @author LLT
 *
 */
public class TestApplicationContext extends AbstractApplicationContext {
	private SpectrumAlgorithm suspiciousnessCalcul;
	protected List<String> projectClasspath;

	public TestApplicationContext() {
		projectClasspath = new ArrayList<String>();
		initClasspath();
	}

	protected void initClasspath() {
		projectClasspath.add(TestConfiguration.SAV_COMMONS_TEST_TARGET);
	}

	@Override
	public List<String> getProjectClasspath() {
		return projectClasspath; 
	}
	
	public void setProjectClasspath(List<String> projectClasspath) {
		this.projectClasspath = projectClasspath;
	}

	@Override
	protected String getTracerJarPath() {
		return TestConfigUtils.getTracerLibPath();
	}

	@Override
	protected String getJavahome() {
		return TestConfigUtils.getJavaHome();
	}

	@Override
	public SpectrumAlgorithm getSuspiciousnessCalculationAlgorithm() {
		return suspiciousnessCalcul;
	}
	
	protected void setSuspiciousnessCalculationAlgorithm(
			SpectrumAlgorithm algorithm) {
		this.suspiciousnessCalcul = algorithm;
	}

	@Override
	public IPrintStream getVmRunnerPrintStream() {
		return new SavPrintStream(System.out);
	}

	@Override
	protected String getAssembly(String assemblyName) {
		return TestConfiguration.getTzAssembly(assemblyName);
	}
}
