/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author LLT
 *
 */
public class AppContext {
	
	@Inject
	private Injector injector;

	public <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}
	
}
