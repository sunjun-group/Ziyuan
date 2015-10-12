/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.SystemPreferences;

/**
 * @author LLT
 * 
 */
public abstract class AbstractTzTest extends AbstractTest {
	protected TestConfiguration config;
	protected TestApplicationContext testContext;
	protected AppJavaClassPath appData;
	
	public AbstractTzTest() {
		config = TestConfiguration.getInstance();
		testContext = new TestApplicationContext();
		this.appData = testContext.getAppData();
		loadPreferences(appData.getPreferences());
	}

	protected void loadPreferences(SystemPreferences preferences) {
		// do nothing by default
	}
}
