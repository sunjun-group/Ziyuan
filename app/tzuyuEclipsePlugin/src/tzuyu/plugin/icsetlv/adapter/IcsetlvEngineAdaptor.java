/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.icsetlv.adapter;

import icsetlv.common.exception.IcsetlvException;

import java.util.List;

import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint;
import tzuyu.plugin.commons.dto.WorkObject;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.icsetlv.command.AnalysisPreferences;



/**
 * @author LLT
 *
 */
public interface IcsetlvEngineAdaptor {

	List<BreakPoint> analyse(WorkObject workObj, AnalysisPreferences prefs)
			throws PluginException, SavException, IcsetlvException;
	
}
