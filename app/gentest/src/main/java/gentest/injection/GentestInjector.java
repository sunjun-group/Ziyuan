/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import gentest.core.data.type.ITypeCreator;
import gentest.core.data.type.VarTypeCreator;
import gentest.core.value.store.SubTypesScanner;
import gentest.core.value.store.TypeMethodCallsCache;
import gentest.core.value.store.VariableCache;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;
import sav.strategies.gentest.ISubTypesScanner;

import com.google.inject.AbstractModule;

/**
 * @author LLT
 *
 */
public class GentestInjector extends AbstractModule {
	private VariableCache cache;
	private SubTypesScanner subTypeScanner;
	private TypeMethodCallsCache typeMethodCallsCache;
	private ITypeCreator varTypes;
	
	public GentestInjector() {
		cache = new VariableCache();
		subTypeScanner = new SubTypesScanner();
		typeMethodCallsCache = new TypeMethodCallsCache();
		varTypes = new VarTypeCreator();
	}

	@Override
	protected void configure() {
		bind(IVariableStore.class).toInstance(cache);
		bind(ISubTypesScanner.class).toInstance(subTypeScanner);
		bind(ITypeMethodCallStore.class).toInstance(typeMethodCallsCache);
		requestInjection(varTypes);
		bind(ITypeCreator.class).toInstance(varTypes);
	}

	public void release() {
		cache.clear();
		subTypeScanner.clear();
		typeMethodCallsCache.clear();
		varTypes = null;
	}
}
