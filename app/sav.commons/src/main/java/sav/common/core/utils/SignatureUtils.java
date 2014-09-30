/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.lang.reflect.Method;

/**
 * @author LLT
 * 
 */
public class SignatureUtils {
	/**
     * Compute the JVM method descriptor for the method.
     */
	public static String getSignature(Method meth) {
		StringBuffer sb = new StringBuffer();

		sb.append("(");

		Class<?>[] params = meth.getParameterTypes(); // avoid clone
		for (int j = 0; j < params.length; j++) {
			sb.append(getSignature(params[j]));
		}
		sb.append(")");
		sb.append(getSignature(meth.getReturnType()));
		return sb.toString();
	}

	/**
	 * Compute the JVM signature for the class.
	 */
	public static String getSignature(Class<?> clazz) {
		String type = null;
		if (clazz.isArray()) {
			Class<?> cl = clazz;
			int dimensions = 0;
			while (cl.isArray()) {
				dimensions++;
				cl = cl.getComponentType();
			}
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < dimensions; i++) {
				sb.append("[");
			}
			sb.append(getSignature(cl));
			type = sb.toString();
		} else if (clazz.isPrimitive()) {
			if (clazz == Integer.TYPE) {
				type = "I";
			} else if (clazz == Byte.TYPE) {
				type = "B";
			} else if (clazz == Long.TYPE) {
				type = "J";
			} else if (clazz == Float.TYPE) {
				type = "F";
			} else if (clazz == Double.TYPE) {
				type = "D";
			} else if (clazz == Short.TYPE) {
				type = "S";
			} else if (clazz == Character.TYPE) {
				type = "C";
			} else if (clazz == Boolean.TYPE) {
				type = "Z";
			} else if (clazz == Void.TYPE) {
				type = "V";
			}
		} else {
			type = getSignature(clazz.getName());
		}
		return type;
	}

	public static String getSignature(String className) {
		return "L" + className.replace('.', '/') + ";";
	}

}
