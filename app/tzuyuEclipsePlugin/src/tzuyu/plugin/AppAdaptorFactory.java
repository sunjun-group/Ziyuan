/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin;

import tzuyu.plugin.icsetlv.adapter.IcsetlvEngineAdaptor;
import tzuyu.plugin.icsetlv.adapter.IcsetlvEngineAdaptorImpl;
import tzuyu.plugin.tester.adapter.TzuyuEngineAdaptor;
import tzuyu.plugin.tester.adapter.TzuyuEngineAdaptorImpl;

/**
 * @author LLT
 *
 */
public class AppAdaptorFactory {
	
	public static TzuyuEngineAdaptor getTzuyuAdaptor() {
		return new TzuyuEngineAdaptorImpl();
	}
	
	public static IcsetlvEngineAdaptor getIcsetlvAdaptor() {
		return new IcsetlvEngineAdaptorImpl();
	}
}
