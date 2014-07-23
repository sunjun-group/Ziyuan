/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.adapter;

import org.eclipse.core.runtime.IProgressMonitor;

import tzuyu.engine.TzClass;
import tzuyu.engine.Tzuyu;
import tzuyu.engine.iface.TzuyuEngine;
import tzuyu.engine.model.exception.TzException;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.console.TzConsole;
import tzuyu.plugin.tester.reporter.GenTestReporter;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public class TzuyuEngineAdaptorImpl implements TzuyuEngineAdaptor {
	
	static {
		Tzuyu.setLogger(PluginLogger.getLogger());
	}
	
	public TzuyuEngineAdaptorImpl() {
		
	}

	@Override
	public void generateTestCases(WorkObject workObject,
			GenTestPreferences config, GenTestReporter reporter, IProgressMonitor monitor)
			throws InterruptedException, TzException {
		try {
			TzConsole.showConsole().clearConsole();
			monitor.beginTask("converting working object", 1);
			TzClass tzProject = ProjectConverter.from(workObject, config);
			monitor.done();
			monitor.beginTask("learning", IProgressMonitor.UNKNOWN);
			TzuyuEngine tzuyu = new Tzuyu(tzProject, reporter,
					new PluginReferencesAnalyzer(workObject.getProject(),
							config));
			tzuyu.run();
			monitor.done();
		} catch (PluginException e) {
			PluginLogger.getLogger().logEx(e);
		} 
	}

}
