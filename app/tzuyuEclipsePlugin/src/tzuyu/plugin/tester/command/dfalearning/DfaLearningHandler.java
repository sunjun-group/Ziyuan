/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.dfalearning;

import org.eclipse.core.runtime.IProgressMonitor;

import tzuyu.engine.model.exception.TzException;
import tzuyu.plugin.AppAdaptorFactory;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.tester.command.gentest.GenTestHandler;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.reporter.GenTestReporter;

/**
 * @author LLT
 *
 */
public class DfaLearningHandler extends GenTestHandler {
	
	@Override
	protected void runJob(WorkObject workObject, GenTestPreferences config,
			GenTestReporter reporter, IProgressMonitor monitor)
			throws InterruptedException, TzException {
		AppAdaptorFactory.getTzuyuAdaptor().dfaLearning(workObject, config,
				reporter, monitor);
	}
}
