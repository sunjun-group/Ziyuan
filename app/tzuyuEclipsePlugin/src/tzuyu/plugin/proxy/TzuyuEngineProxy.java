/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.proxy;

import tzuyu.engine.TzProject;
import tzuyu.engine.Tzuyu;
import tzuyu.engine.iface.TzReportHandler;
import tzuyu.engine.iface.TzuyuEngine;
import tzuyu.plugin.action.testgen.GenTestConfiguration;
import tzuyu.plugin.core.dto.WorkObject;
import tzuyu.plugin.core.exception.PluginException;
import tzuyu.plugin.reporter.PluginLogger;
import tzuyu.plugin.reporter.Reporter;

/**
 * @author LLT
 * 
 */
public class TzuyuEngineProxy implements TzuyuEngine {
	private Tzuyu tzuyu;

	public TzuyuEngineProxy(TzProject project, TzReportHandler reporter) {
		tzuyu = new Tzuyu(project, reporter);
	}

	@Override
	public void run() {
		tzuyu.run();
	}

	public static void run(WorkObject workObject, GenTestConfiguration config) {
		try {
			TzProject tzProject = ProjectConverter.from(workObject, config);
			new TzuyuEngineProxy(tzProject, new Reporter(tzProject.getConfiguration())).run();
		} catch (PluginException e) {
			PluginLogger.logEx(e);
		}
	}

}
