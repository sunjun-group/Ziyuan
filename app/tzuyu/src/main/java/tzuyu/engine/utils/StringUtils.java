/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import java.util.Arrays;

/**
 * @author LLT
 * 
 */
public class StringUtils {
	private StringUtils() {
	}

	public static String spaceJoin(Object...params) {
		return join(" ", params);
	}
	
	public static String join(String separator, Object... params) {
		return org.apache.commons.lang.StringUtils.join(Arrays.asList(params),
				separator);
	}
	
	public static <T extends Object> String dotJoin(T[] params) {
		return org.apache.commons.lang.StringUtils.join(params, ".");
	}
}
