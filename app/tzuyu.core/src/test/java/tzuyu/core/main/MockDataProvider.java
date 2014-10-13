/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;


import java.util.List;

import sav.common.core.utils.CollectionUtils;
import sav.commons.TestConfiguration;
import sav.commons.utils.ConfigUtils;
import tzuyu.core.main.AbstractDataProvider;


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
		return CollectionUtils.listOf(testConfig
				.getTestTarget(TestConfiguration.getInstance().testTarget));
	}

	@Override
	protected String getTracerJarPath() {
		return ConfigUtils.getTracerLibPath();
	}
}
