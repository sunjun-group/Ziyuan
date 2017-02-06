/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.adapter;

import org.eclipse.core.runtime.IProgressMonitor;

import tzuyu.engine.model.exception.TzException;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.reporter.GenTestReporter;

/**
 * @author LLT
 *
 */
public interface TzuyuEngineAdaptor {

	void dfaLearning(WorkObject workObject, GenTestPreferences config,
			GenTestReporter reporter, IProgressMonitor monitor)
			throws InterruptedException, TzException;

	void generateTestcases(WorkObject workObject, GenTestPreferences config,
			GenTestReporter reporter, IProgressMonitor monitor)
			throws InterruptedException, TzException;

}
