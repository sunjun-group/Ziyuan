/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import icsetlv.common.Constants;
import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.ClassLocation;

import java.util.ArrayList;
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
	
	public static <T extends ClassLocation>String getLocationId(T bkp) {
		return getLocationId(bkp.getClassCanonicalName(), bkp.getLineNo());
	}
	
	public static String getLocationId(String classPath, int lineNo) {
		return String.format("%s:%s", classPath.replace(Constants.FILE_SEPARATOR, "."), lineNo);
	}

	public static <T extends ClassLocation> List<String> toLocationIds(List<T> bkps) {
		List<String> locs = new ArrayList<String>(bkps.size());
		for (T bkp : bkps) {
			locs.add(getLocationId(bkp));
		}
		return locs;
	}
	
}
