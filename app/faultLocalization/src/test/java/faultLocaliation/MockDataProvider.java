/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import icsetlv.TestConfiguration;

import java.util.List;

import main.AbstractDataProvider;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class MockDataProvider extends AbstractDataProvider {
	private TestConfiguration testConfig;
	
	public MockDataProvider() {
		testConfig = TestConfiguration.getInstance();
	}

	@Override
	protected String getJavaHome() {
		return testConfig.getJavahome();
	}

	@Override
	protected String getTracerJarPath() {
		return testConfig.tracerLibPath;
	}

	@Override
	protected List<String> getProjectClasspath() {
		return CollectionUtils.listOf(testConfig.getTestTarget(TestConfiguration.FALTLOCALISATION));
	}
	
}
