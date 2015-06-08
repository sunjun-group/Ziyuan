/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
class ApplicationContext implements IApplicationContext {
	private Injector injector;
	
	private ApplicationContext(Injector injector) {
		this.injector = injector;
	}
	/**
	 * TODO LLT: [NICE TO HAVE] remove the current IApplicationContext,
	 * and using Guice in other modules.
	 */
	public static ApplicationContext loadContext(Module modules) {
		Injector injector = Guice.createInjector(modules);
		return new ApplicationContext(injector);
	}
	
	public <T> T getInstance(Class<T> type) {
		return injector.getInstance(type);
	}

	@Override
	public ISlicer getSlicer() {
		return getInstance(ISlicer.class);
	}

	@Override
	public ICodeCoverage getCodeCoverageTool() {
		return getInstance(ICodeCoverage.class);
	}
	/* (non-Javadoc)
	 * @see sav.strategies.IApplicationContext#getMutator()
	 */
	@Override
	public IMutator getMutator() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see sav.strategies.IApplicationContext#getVmConfig()
	 */
	@Override
	public VMConfiguration getVmConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
