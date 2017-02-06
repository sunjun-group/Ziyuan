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

import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import tzuyu.core.main.context.AbstractApplicationContext;


/**
 * @author LLT
 *
 */
public class TestApplicationContext extends AbstractApplicationContext {

	public TestApplicationContext() {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		List<String> projectClasspath = new ArrayList<String>();
		projectClasspath.add(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		appClasspath.addClasspaths(projectClasspath);
		appClasspath.setJavaHome(TestConfiguration.getJavaHome());
		appClasspath.setSrc(TestConfiguration.getTestScrPath("sav.commons"));
		appClasspath.setTarget(TestConfiguration.getTestTarget("sav.commons"));
		appClasspath.setTestTarget(appClasspath.getTarget());
		setAppData(appClasspath);
	}
}
