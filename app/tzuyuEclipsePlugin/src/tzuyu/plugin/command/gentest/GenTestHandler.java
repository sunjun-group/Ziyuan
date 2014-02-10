/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command.gentest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.command.TzCommandHandler;
import tzuyu.plugin.command.TzJob;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.core.utils.IStatusUtils;
import tzuyu.plugin.proxy.TzuyuEngineProxy;

/**
 * @author LLT
 *
 */
public class GenTestHandler extends TzCommandHandler<GenTestPreferences> {

	@Override
	protected void run(WorkObject workObject, GenTestPreferences config) {
		GenTestJob job = new GenTestJob("Generate testcases", workObject, config);
		job.scheduleJob();
	}

	@Override
	protected GenTestPreferences initConfiguration(WorkObject workObject) {
		return TzuyuPlugin.getDefault().getGenTestPreferences(workObject.getProject());
	}
	
	private static class GenTestJob extends TzJob {
		private WorkObject workObject;
		private GenTestPreferences config;

		public GenTestJob(String name, WorkObject workObject, GenTestPreferences config) {
			super(name);
			this.workObject = workObject;
			this.config = config;
		}

		@Override
		protected IStatus doJob(IProgressMonitor monitor) {
			TzuyuEngineProxy.generateTestCases(workObject, config);
			monitor.done();
			return IStatusUtils.OK_STATUS;
		}
		
	}
}
