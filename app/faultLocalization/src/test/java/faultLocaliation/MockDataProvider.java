/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;


import java.util.List;

import main.AbstractDataProvider;
import sav.common.core.utils.CollectionUtils;

import commons.TestConfiguration;

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
	protected List<String> getProjectClasspath() {
		return CollectionUtils.listOf(testConfig.getTestTarget(TestConfiguration.FALTLOCALISATION));
	}
	
}
