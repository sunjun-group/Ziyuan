/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import org.junit.Before;

import sav.commons.TestConfiguration;

/**
 * @author LLT
 * 
 */
public class AbstractGTTest {
	protected TestConfiguration config = TestConfiguration.getInstance();
	protected String srcPath;

	@Before
	public void beforeMethod() {
		srcPath = config.getTestScrPath("gentest");
	}

}
