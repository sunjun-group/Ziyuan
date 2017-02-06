/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tester;

import tzuyu.engine.utils.PrimitiveTypes;

/**
 * @author LLT
 *
 */
public enum ObjectType {
	PRIMITIVE_TYPE, // int, char, double, float,..
	STRING_OR_PRIMITIVE_OBJECT, // Integer, Double, String
	ENUM, // specific enum type
	GENERIC_ENUM, // Enum<?>
	GENERIC_CLASS, // Class<?>
	GENERIC_OBJ,
	ARRAY,
	INTERFACE,
	OTHER_OBJECT;
	
	public static ObjectType ofClass(Class<?> type) {
		if (type == null) {
			return null;
		}
		if (type == Enum.class) {
			return GENERIC_ENUM;
		}
		if (type == Class.class) {
			return GENERIC_CLASS;
		}
		if (type == Object.class) {
			return GENERIC_OBJ;
		}
		if (PrimitiveTypes.isPrimitive(type)) {
			return PRIMITIVE_TYPE;
		}
		if (PrimitiveTypes.isBoxedPrimitiveTypeOrString(type)) {
			return STRING_OR_PRIMITIVE_OBJECT;
		}
		if (type.isEnum()) {
			return ENUM;
		}
		if (type.isArray()) {
			return ARRAY;
		}
		return OTHER_OBJECT;
	}
}
