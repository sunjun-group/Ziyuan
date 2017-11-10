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
public class ArrayTypeUtils {
	private ArrayTypeUtils() {
	}

	public static int getArrayDimension(Class<?> type) {
		int dimension = 0;
		Class<?> arrayType = type;
		while (arrayType.isArray()) {
			arrayType = arrayType.getComponentType();
			dimension++;
		}
		return dimension;
	}
	
	public static Class<?> getContentClass(Class<?> clazz) {
		Class<?> contentClazz = clazz;
		while(contentClazz.isArray()) {
			contentClazz = contentClazz.getComponentType();
		}
		return contentClazz;
	}
}
