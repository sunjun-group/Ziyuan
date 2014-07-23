/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.icsetlv.adapter;

import java.util.List;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.icsetlv.command.AnalysisPreferences;



/**
 * @author LLT
 *
 */
public interface IcsetlvEngineAdaptor {

	List<BreakPoint> analyse(WorkObject workObj, AnalysisPreferences prefs)
			throws IcsetlvException, PluginException;
	
}
