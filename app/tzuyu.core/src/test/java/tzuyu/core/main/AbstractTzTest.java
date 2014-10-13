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

/**
 * @author LLT
 * 
 */
public class AbstractTzTest extends AbstractTest {
	protected TestConfiguration config;
	protected TestApplicationContext testContext;
	
	public AbstractTzTest() {
		config = TestConfiguration.getInstance();
		testContext = new TestApplicationContext();
	}
}
