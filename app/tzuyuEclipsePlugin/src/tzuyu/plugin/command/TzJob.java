/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author LLT
 *
 */
public abstract class TzJob extends Job {

	public TzJob(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		return null;
	}

	
}
