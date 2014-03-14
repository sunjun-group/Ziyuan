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
import tzuyu.plugin.reporter.GenTestReporter;

/**
 * @author LLT
 *
 */
public class GenTestHandler extends TzCommandHandler<GenTestPreferences> {

	@Override
	protected void run(WorkObject workObject, final GenTestPreferences config) {
		final GenTestReporter reporter = new GenTestReporter(config);
		GenTestJob job = new GenTestJob("Generate testcases", workObject,
				config, reporter);
		job.scheduleJob();
	}
	
	@Override
	protected GenTestPreferences initConfiguration(WorkObject workObject) {
		return TzuyuPlugin.getDefault().getGenTestPreferences(workObject.getProject());
	}
	
	private static class GenTestJob extends TzJob {
		private WorkObject workObject;
		private GenTestPreferences config;
		private GenTestReporter reporter;

		public GenTestJob(String name, WorkObject workObject,
				GenTestPreferences config, GenTestReporter reporter) {
			super(name);
			this.workObject = workObject;
			this.config = config;
			this.reporter = reporter;
		}

		@Override
		protected IStatus doJob(IProgressMonitor monitor) {
			TzuyuEngineProxy.generateTestCases(workObject, config, reporter);
			// refresh output folder
			monitor.done();
			return IStatusUtils.OK_STATUS;
		}
		
	}
}
