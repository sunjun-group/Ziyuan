/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import java.util.Arrays;
import java.util.List;

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
	
	public static String dotJoinStr(String... params) {
		return org.apache.commons.lang.StringUtils.join(params, ".");
	}
	
	public static String toStringNullToEmpty(Object val) {
		if (val == null) {
			return org.apache.commons.lang.StringUtils.EMPTY;
		}
		return val.toString();
	}
	
	public static boolean isStartWithUppercaseLetter(String text) {
		if (org.apache.commons.lang.StringUtils.isEmpty(text)) {
			return false;
		}
		return Character.isUpperCase(text.charAt(0));
	}

	public static String newLineJoin(List<String> value) {
		return org.apache.commons.lang.StringUtils.join(value, "\n");
	}
}
