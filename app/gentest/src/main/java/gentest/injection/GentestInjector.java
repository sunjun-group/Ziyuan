/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import sav.strategies.gentest.ISubTypesScanner;
import gentest.core.value.VariableCache;
import gentest.core.value.store.iface.IVariableCache;
import gentest.service.impl.SubTypesScanner;

import com.google.inject.AbstractModule;

/**
 * @author LLT
 *
 */
public class GentestInjector extends AbstractModule {
	private VariableCache cache;
	private SubTypesScanner subTypeScanner;
	
	public GentestInjector() {
		cache = new VariableCache();
		subTypeScanner = new SubTypesScanner();
	}

	@Override
	protected void configure() {
		bind(IVariableCache.class).toInstance(cache);
		bind(ISubTypesScanner.class).toInstance(subTypeScanner);
	}

	public void release() {
		cache.reset();
		subTypeScanner.reset();
	}
}
