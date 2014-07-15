/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;


import java.util.Arrays;
import java.util.Collection;

/**
 * @author LLT
 * 
 */
public class StringUtils {
	public static final String EMPTY = "";

	private StringUtils() {
	}

	public static boolean isEmpty(final String str) {
		return str == null || str.length() == 0;
	}

	public static String join(Collection<?> vals, String separator) {
		if (CollectionUtils.isEmpty(vals)) {
			return EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (Object val : vals) {
			String valStr = toStringNullToEmpty(val);
			if (!isEmpty(valStr)) {
				if (sb.length() != 0) {
					sb.append(separator);
				}
				sb.append(valStr);
			}
		}
		return sb.toString();
	}

	public static String join(String separator, Object... params) {
		return join(Arrays.asList(params), separator);
	}

	public static String spaceJoin(Object... params) {
		return join(" ", params);
	}

	public static String dotJoin(Object... params) {
		return join(Arrays.asList(params), ".");
	}

	public static String toStringNullToEmpty(Object val) {
		return toString(val, EMPTY);
	}

	public static String toString(Object val, String defaultIfNull) {
		if (val == null) {
			return defaultIfNull;
		}
		return val.toString();
	}

	public static String nullToEmpty(String val) {
		if (val == null) {
			return EMPTY;
		}
		return val;
	}

	public static String[] nullToEmpty(String[] val) {
		if (val == null) {
			return new String[0];
		}
		return val;
	}
	
	

}
