/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import sav.commons.AbstractTest;
import sav.strategies.IDataProvider;

/**
 * @author LLT
 * 
 */
public class AbstractTzTest extends AbstractTest {
	
	protected IDataProvider getDataProvider() {
		return new MockDataProvider();
	}
}
