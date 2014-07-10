/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.commons.utils.IStatusUtils;

/**
 * @author LLT
 *
 */
public abstract class TzJob extends Job {
	public TzJob(String name) {
		super(name);
	}

	@Override
	protected final IStatus run(final IProgressMonitor monitor) {
		try {
			doJob(monitor);
		} finally {
			monitor.done();
		}
		return IStatusUtils.OK_STATUS;
	}
	
	protected abstract IStatus doJob(IProgressMonitor monitor);

	@Override
	public boolean belongsTo(Object family) {
		return family == TzuyuPlugin.class;
	}
	
	public void scheduleJob() {
		setUser(true);
		setPriority(Job.INTERACTIVE);
		schedule();
	}
}
