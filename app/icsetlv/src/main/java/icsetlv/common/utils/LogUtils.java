/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import sav.common.core.utils.StringUtils;


/**
 * @author LLT
 *
 */
public class LogUtils {
	private LogUtils(){}

	public static void log(Object... objs) {
		System.out.println(StringUtils.spaceJoin(objs));
	}

}
