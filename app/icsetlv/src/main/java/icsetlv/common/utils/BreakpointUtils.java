/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import icsetlv.common.dto.BreakPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class BreakpointUtils {
	private BreakpointUtils(){}
	
	public static Map<String, List<BreakPoint>> initBrkpsMap(
			List<BreakPoint> brkps) {
		HashMap<String, List<BreakPoint>> brkpsMap = new HashMap<String, List<BreakPoint>>();
		for (BreakPoint brkp : brkps) {
			List<BreakPoint> bps = CollectionUtils.getListInitIfEmpty(brkpsMap,
					brkp.getClassCanonicalName());
			bps.add(brkp);
		}
		return brkpsMap;
	}
	
	public static String getLocationId(BreakPoint bkp) {
		return String.format("%s:%s", bkp.getClassCanonicalName(), bkp.getLineNo());
	}
	
}
