/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.TzCommandHandler;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.proxy.TzuyuEngineProxy;

/**
 * @author LLT
 *
 */
public class GenTestHandler extends TzCommandHandler<GenTestPreferences> {

	@Override
	protected void run(WorkObject workObject, GenTestPreferences config) {
		TzuyuEngineProxy.run(workObject, config); 
		TzuyuPlugin.getDefault().persistGenTestPreferences(workObject.getProject().getProject(), config);
	}

	@Override
	protected GenTestPreferences initConfiguration(WorkObject workObject) {
		return TzuyuPlugin.getDefault().getGenTestPreferences(workObject.getProject());
	}
	
}
