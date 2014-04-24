/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.proxy;

import org.eclipse.core.runtime.IProgressMonitor;

import tzuyu.engine.TzClass;
import tzuyu.engine.Tzuyu;
import tzuyu.engine.iface.IReferencesAnalyzer;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.TzuyuEngine;
import tzuyu.engine.model.exception.ReportException;
import tzuyu.engine.model.exception.TzException;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.console.TzConsole;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.core.exception.PluginException;
import tzuyu.plugin.reporter.GenTestReporter;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public class TzuyuEngineProxy implements TzuyuEngine {
	private Tzuyu tzuyu;
	
	static {
		Tzuyu.setLogger(PluginLogger.getLogger());
	}

	public TzuyuEngineProxy(TzClass project, TzReportHandler reporter,
			IReferencesAnalyzer refAnalyzer) {
		tzuyu = new Tzuyu(project, reporter, refAnalyzer);
	}

	public void run() throws ReportException, InterruptedException, TzException {
		tzuyu.run();
	}

	public static void generateTestCases(WorkObject workObject,
			GenTestPreferences config, GenTestReporter reporter, IProgressMonitor monitor)
			throws InterruptedException, TzException {
		try {
			TzConsole.showConsole().clearConsole();
			monitor.beginTask("converting working object", 1);
			TzClass tzProject = ProjectConverter.from(workObject, config);
			monitor.done();
			monitor.beginTask("learning", IProgressMonitor.UNKNOWN);
			new TzuyuEngineProxy(tzProject, reporter,
					new PluginReferencesAnalyzer(workObject.getProject(), config)).run();
			monitor.done();
		} catch (PluginException e) {
			PluginLogger.getLogger().logEx(e);
		} 
	}

}
