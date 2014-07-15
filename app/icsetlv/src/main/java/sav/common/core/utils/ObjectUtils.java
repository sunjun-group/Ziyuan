/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

/**
 * @author LLT
 * 
 */
public class ObjectUtils {

	public static boolean equalsWithNull(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}
	
	public static int compare(int o1, int o2) {
		return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
	}
	
	public static Class<?> getObjClass(Object obj, Class<?> defaultIfNull) {
		if (obj == null) {
			return defaultIfNull;
		}
		if (obj instanceof Class) {
			return (Class<?>) obj;
		}
		return obj.getClass();
	}
}
