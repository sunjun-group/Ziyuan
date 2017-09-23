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
public class PrimitiveUtils {
	private PrimitiveUtils(){}
	
	private static String STRING_TYPE = String.class.getName();
	
	private static String BOOLEAN_TYPE = Boolean.class.getName();
	
	private static String BYTE_TYPE = Byte.class.getName();
	
	private static String CHAR_TYPE = Character.class.getName();
	
	private static String DOUBLE_TYPE = Double.class.getName();
	
	private static String FLOAT_TYPE = Float.class.getName();
	
	private static String INTEGER_TYPE = Integer.class.getName();
	
	private static String LONG_TYPE = Long.class.getName();
	
	private static String SHORT_TYPE = Short.class.getName();
	
	public static String[] PRIMITIVE_TYPES = new String[]{
			INTEGER_TYPE,
			BOOLEAN_TYPE,
			FLOAT_TYPE,
			CHAR_TYPE,
			DOUBLE_TYPE,
			LONG_TYPE,
			SHORT_TYPE,
			BYTE_TYPE,
		};
	
	public static boolean isPrimitiveType(String clazzName) {
		return CollectionUtils.existIn(clazzName, PRIMITIVE_TYPES);
	}
	
	public static boolean isPrimitive(String type){
		if(type.equals("int") ||
				type.equals("boolean") ||
				type.equals("float") ||
				type.equals("char") ||
				type.equals("double") ||
				type.equals("long") ||
				type.equals("short") ||
				type.equals("byte")){
			return true;
		}
		
		return false;
	}
	
	public static boolean isPrimitiveTypeOrString(String clazzName) {
		return isPrimitiveType(clazzName) || isString(clazzName);
	}

	public static boolean isString(String clazzName) {
		return STRING_TYPE.equals(clazzName);
	}
	
	public static boolean isBooleanType(String clazzName) {
		return BOOLEAN_TYPE.equals(clazzName);
	}
	
	public static boolean isByteType(String clazzName) {
		return BYTE_TYPE.equals(clazzName);
	}
	
	public static boolean isCharType(String clazzName) {
		return CHAR_TYPE.equals(clazzName);
	}
	
	public static boolean isDoubleType(String clazzName) {
		return DOUBLE_TYPE.equals(clazzName);
	}
	
	public static boolean isFloatType(String clazzName) {
		return FLOAT_TYPE.equals(clazzName);
	}
	
	public static boolean isIntegerType(String clazzName) {
		return INTEGER_TYPE.equals(clazzName);
	}
	
	public static boolean isLongType(String clazzName) {
		return LONG_TYPE.equals(clazzName);
	}
	
	public static boolean isShortType(String clazzName) {
		return SHORT_TYPE.equals(clazzName);
	}
	
	public static boolean isBoolean(String clazzName) {
		return "boolean".equals(clazzName) || BOOLEAN_TYPE.equals(clazzName);
	}
	
	public static boolean isByte(String clazzName) {
		return "byte".equals(clazzName) || BYTE_TYPE.equals(clazzName);
	}
	
	public static boolean isChar(String clazzName) {
		return "char".equals(clazzName) || CHAR_TYPE.equals(clazzName);
	}
	
	public static boolean isDouble(String clazzName) {
		return "double".equals(clazzName) || DOUBLE_TYPE.equals(clazzName);
	}
	
	public static boolean isFloat(String clazzName) {
		return "float".equals(clazzName) || FLOAT_TYPE.equals(clazzName);
	}
	
	public static boolean isInteger(String clazzName) {
		return "int".equals(clazzName) || INTEGER_TYPE.equals(clazzName);
	}
	
	public static boolean isLong(String clazzName) {
		return "long".equals(clazzName) || LONG_TYPE.equals(clazzName);
	}
	
	public static boolean isShort(String clazzName) {
		return "short".equals(clazzName) || SHORT_TYPE.equals(clazzName);
	}
}
