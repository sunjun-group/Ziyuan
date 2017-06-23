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
public class ClassLoaderUtils {
	private ClassLoaderUtils() {}

	public static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
		if (classLoader == null) {
			 return Class.forName(className);
		}
		return classLoader.loadClass(className);
	}
	
	
}
