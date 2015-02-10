/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gentest.core.data.typeinitilizer.TypeInitializer;
import gentest.core.value.store.iface.ITypeMethodCallStore;

/**
 * @author LLT
 *
 */
public class TypeMethodCallsCache implements ITypeMethodCallStore {
	private Map<Class<?>, TypeInitializer> constructors;
	private Map<Class<?>, List<List<Method>>> methodCalls;

	public TypeMethodCallsCache() {
		constructors = new HashMap<Class<?>, TypeInitializer>();
		methodCalls = new HashMap<Class<?>, List<List<Method>>>();
	}
	
	public void clear() {
		constructors.clear();
		methodCalls.clear();
	}

	@Override
	public TypeInitializer loadConstructors(Class<?> type) {
		return constructors.get(type);
	}

	@Override
	public void storeConstructors(Class<?> type, TypeInitializer typeConstructors) {
		constructors.put(type, typeConstructors);
	}
	
}
