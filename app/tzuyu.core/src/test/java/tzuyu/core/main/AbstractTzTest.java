/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import sav.commons.AbstractTest;
import tzuyu.core.main.context.AbstractApplicationContext;

/**
 * @author LLT
 * 
 */
public class AbstractTzTest extends AbstractTest {
	protected AbstractApplicationContext appContext;
	
	public AbstractTzTest() {
		appContext = new TestApplicationContext();
	}
}
