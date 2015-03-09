/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import gentest.core.data.Sequence;
import gentest.core.data.dto.DataProvider;
import gentest.core.data.dto.IDataProvider;
import gentest.core.data.type.ITypeCreator;
import gentest.core.data.type.VarTypeCreator;
import gentest.core.value.store.SubTypesScanner;
import gentest.core.value.store.TypeMethodCallsCache;
import gentest.core.value.store.VariableCache;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sav.strategies.gentest.ISubTypesScanner;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * @author LLT
 *
 */
public class GentestModules extends AbstractModule {
	private Map<Class<? extends Annotation>, EnterableScope> scopes;
	
	public GentestModules() {
		scopes = new HashMap<Class<? extends Annotation>, EnterableScope>();
		scopes.put(TestcaseGenerationScope.class, new EnterableScope());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		for (Entry<Class<? extends Annotation>, EnterableScope> scope : scopes
				.entrySet()) {
			bindScope(scope.getKey(), scope.getValue());
		}
		bind(IVariableStore.class).to(VariableCache.class);
		bind(ISubTypesScanner.class).to(SubTypesScanner.class);
		bind(ITypeMethodCallStore.class).to(TypeMethodCallsCache.class);
		bind(ITypeCreator.class).to(VarTypeCreator.class);
		bind(new TypeLiteral<IDataProvider<Sequence>>(){})
				.to((Class<? extends IDataProvider<Sequence>>) DataProvider.class)
				.in(scopes.get(TestcaseGenerationScope.class));
	}

	public void enter(Class<? extends Annotation> scope) {
		scopes.get(scope).enter();
	}
	
	public void exit(Class<? extends Annotation> scope) {
		scopes.get(scope).exit();
	}
}
