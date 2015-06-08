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
import tzuyu.core.inject.ApplicationData;

/**
 * @author LLT
 * 
 */
public class AbstractTzTest extends AbstractTest {
	protected TestConfiguration config;
	protected TestApplicationContext testContext;
	protected ApplicationData appData;
	
	public AbstractTzTest() {
		config = TestConfiguration.getInstance();
		testContext = new TestApplicationContext();
		this.appData = testContext.getAppData();
	}
}
