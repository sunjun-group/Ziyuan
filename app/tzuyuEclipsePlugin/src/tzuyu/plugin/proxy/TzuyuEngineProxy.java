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

}
