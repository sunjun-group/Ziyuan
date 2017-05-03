/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author LLT
 *
 */
public abstract class AbstractLearntestHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Job job = new Job(getJobName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return execute(monitor);
			}
		};
		job.schedule();

		return null;
	}
	
	protected void handleException(Exception e) {
		e.printStackTrace();
	}

	protected abstract IStatus execute(IProgressMonitor monitor);

	protected abstract String getJobName();

}
