/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class PrimitiveUtils {
	private PrimitiveUtils(){}
	
	public static String[] PRIMITIVE_TYPES = new String[]{
		Integer.class.getName(),
		Boolean.class.getName(),
		Float.class.getName(),
		Character.class.getName(),
		Double.class.getName(),
		Long.class.getName(),
		Short.class.getName(),
		Byte.class.getName(),
	};
	
	private static String STRING_TYPE = String.class.getName();
	
	private static String BOOLEAN_TYPE = Boolean.class.getName();
	
	private static String BYTE_TYPE = Byte.class.getName();
	
	private static String CHAR_TYPE = Character.class.getName();
	
	private static String DOUBLE_TYPE = Double.class.getName();
	
	private static String FLOAT_TYPE = Float.class.getName();
	
	private static String INTEGER_TYPE = Integer.class.getName();
	
	private static String LONG_TYPE = Long.class.getName();
	
	private static String SHORT_TYPE = Short.class.getName();
	
	public static boolean isPrimitiveType(String clazzName) {
		return CollectionUtils.existIn(clazzName, PRIMITIVE_TYPES);
	}
	
	public static boolean isPrimitiveTypeOrString(String clazzName) {
		return isPrimitiveType(clazzName) || isString(clazzName);
	}

	public static boolean isString(String clazzName) {
		return STRING_TYPE.equals(clazzName);
	}
	
	public static boolean isBoolean(String clazzName) {
		return BOOLEAN_TYPE.equals(clazzName);
	}
	
	public static boolean isByte(String clazzName) {
		return BYTE_TYPE.equals(clazzName);
	}
	
	public static boolean isChar(String clazzName) {
		return CHAR_TYPE.equals(clazzName);
	}
	
	public static boolean isDouble(String clazzName) {
		return DOUBLE_TYPE.equals(clazzName);
	}
	
	public static boolean isFloat(String clazzName) {
		return FLOAT_TYPE.equals(clazzName);
	}
	
	public static boolean isInteger(String clazzName) {
		return INTEGER_TYPE.equals(clazzName);
	}
	
	public static boolean isLong(String clazzName) {
		return LONG_TYPE.equals(clazzName);
	}
	
	public static boolean isShort(String clazzName) {
		return SHORT_TYPE.equals(clazzName);
	}
	
}
