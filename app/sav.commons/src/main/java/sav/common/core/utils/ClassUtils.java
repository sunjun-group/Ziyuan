/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import sav.common.core.Constants;
import sav.common.core.Pair;


/**
 * @author LLT
 *
 */
public class ClassUtils {
	private ClassUtils() {}

	public static String getCanonicalName(String pkg, String clName) {
		return StringUtils.dotJoin(pkg, clName);
	}
	
	/**
	 * very weak method. only handle very simple case of className.
	 */
	public static String getJFilePath(String sourcePath, String className) {
		return sourcePath + Constants.FILE_SEPARATOR
				+ className.replace(".", Constants.FILE_SEPARATOR)
				+ Constants.JAVA_EXT;
	}
	
	public static String getSimpleName(String className) {
		int idx = className.lastIndexOf(".");
		if (idx > 0) {
			return className.substring(idx + 1);
		}
		return className;
	}
	
	/**
	 * return pair of class name, and method name
	 */
	public static Pair<String, String> splitClassMethod(String name) {
		int idx = name.lastIndexOf(".");
		if (idx > 0) {
			return Pair.of(name.substring(0, idx), 
					name.substring(idx + 1));
		}
		throw new IllegalArgumentException(
				"Cannot extract method from string, expect [classname].[method], get "
						+ name);
	}
}
