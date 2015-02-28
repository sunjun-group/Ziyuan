/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import org.junit.Before;

import sav.common.core.Logger;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;

/**
 * @author LLT
 * 
 */
public class AbstractGTTest extends AbstractTest {
	protected Logger<?> log = Logger.getDefaultLogger();
	protected TestConfiguration config = TestConfiguration.getInstance();
	protected String srcPath;

	@Before
	public void beforeMethod() {
		srcPath = config.getTestScrPath("gentest");
	}

}
