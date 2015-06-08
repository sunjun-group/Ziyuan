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

import sav.common.core.Constants;
import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.main.context.AbstractApplicationContext;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;


/**
 * @author LLT
 *
 */
public class TestApplicationContext extends AbstractApplicationContext {
	private SpectrumAlgorithm suspiciousnessCalcul;

	public TestApplicationContext() {
		ApplicationData appData = new ApplicationData();
		List<String> projectClasspath = new ArrayList<String>();
		projectClasspath.add(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		appData.setClasspaths(projectClasspath);
		appData.setJavaHome(TestConfigUtils.getJavaHome());
		appData.setSuspiciousCalculAlgo(suspiciousnessCalcul);
		appData.setTzuyuJacocoAssembly(TestConfiguration.getTzAssembly(Constants.TZUYU_JACOCO_ASSEMBLY));
		appData.setAppSrc(TestConfiguration.getTestScrPath("sav.commons"));
		appData.setAppTarget(TestConfiguration.getTestTarget("sav.commons"));
		appData.setAppTestTarget(appData.getAppTarget());
		setAppData(appData);
	}
}
