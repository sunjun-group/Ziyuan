/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.injection;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import gentest.core.ParamGeneratorConfig;
import gentest.core.data.DataProvider;
import gentest.core.data.IDataProvider;
import gentest.core.data.Sequence;
import gentest.core.data.type.ITypeCreator;
import gentest.core.data.type.VarTypeCreator;
import gentest.core.execution.VariableRuntimeExecutor;
import gentest.core.value.store.SubTypesScanner;
import gentest.core.value.store.TypeMethodCallsCache;
import gentest.core.value.store.VariableCache;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.core.value.store.iface.IVariableStore;
import sav.strategies.gentest.ISubTypesScanner;

/**
 * @author LLT
 *
 */
public class GentestModules extends AbstractModule {
	private static final long DEFAULT_METHOD_EXECUTION_TIMEOUT = 200l;
	private Map<Class<? extends Annotation>, EnterableScope> scopes;
	private ClassLoader prjClassLoader;
	private long methodExecTimeout;
	
	public GentestModules() {
		scopes = new HashMap<Class<? extends Annotation>, EnterableScope>();
		scopes.put(TestcaseGenerationScope.class, new EnterableScope());
	}
	
	public GentestModules(ClassLoader prjClassLoader) {
		this();
		this.prjClassLoader = prjClassLoader;
	}

	@Override
	protected void configure() {
		for (Entry<Class<? extends Annotation>, EnterableScope> scope : scopes
				.entrySet()) {
			bindScope(scope.getKey(), scope.getValue());
		}
		bind(ClassLoader.class).annotatedWith(Names.named("prjClassLoader")).toInstance(getPrjClassLoader());
		bind(IVariableStore.class).to(VariableCache.class);
		bind(ISubTypesScanner.class).to(SubTypesScanner.class);
		bind(ITypeMethodCallStore.class).to(TypeMethodCallsCache.class);
		bind(ITypeCreator.class).to(VarTypeCreator.class);
		bind(new TypeLiteral<IDataProvider<Sequence>>() {})
				.toInstance(new DataProvider<Sequence>());
				// .to((Class<? extends IDataProvider<?>>) DataProvider.class)
				// .in(scopes.get(TestcaseGenerationScope.class));
		bind(ParamGeneratorConfig.class).toInstance(ParamGeneratorConfig.getDefault());
		bind(long.class).annotatedWith(Names.named("methodExecTimeout")).toInstance(getMethodExecTimeout());
		requestStaticInjection(VariableRuntimeExecutor.class);
	}
	
	public ClassLoader getPrjClassLoader() {
		if (prjClassLoader == null) {
			prjClassLoader = Thread.currentThread().getContextClassLoader();
		}
		return prjClassLoader;
	}
	
	public long getMethodExecTimeout() {
		if (methodExecTimeout <= 0) {
			methodExecTimeout = DEFAULT_METHOD_EXECUTION_TIMEOUT;
		}
		return methodExecTimeout;
	}
	
	public void setMethodExecTimeout(long methodExecTimeout) {
		this.methodExecTimeout = methodExecTimeout;
	}

	public void enter(Class<? extends Annotation> scope) {
		scopes.get(scope).enter();
	}
	
	public void exit(Class<? extends Annotation> scope) {
		scopes.get(scope).exit();
	}
}
