/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import sav.strategies.gentest.ISubTypesScanner;
import gentest.core.value.store.SubTypesScanner;
import gentest.core.value.store.TypeMethodCallsCache;
import gentest.core.value.store.VariableCache;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;

import com.google.inject.AbstractModule;

/**
 * @author LLT
 *
 */
public class GentestInjector extends AbstractModule {
	private VariableCache cache;
	private SubTypesScanner subTypeScanner;
	private TypeMethodCallsCache typeMethodCallsCache;
	
	public GentestInjector() {
		cache = new VariableCache();
		subTypeScanner = new SubTypesScanner();
		typeMethodCallsCache = new TypeMethodCallsCache();
	}

	@Override
	protected void configure() {
		bind(IVariableStore.class).toInstance(cache);
		bind(ISubTypesScanner.class).toInstance(subTypeScanner);
		bind(ITypeMethodCallStore.class).toInstance(typeMethodCallsCache);
	}

	public void release() {
		cache.clear();
		subTypeScanner.clear();
		typeMethodCallsCache.clear();
	}
}
