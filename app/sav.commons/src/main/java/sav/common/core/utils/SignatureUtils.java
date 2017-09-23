/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.lang.reflect.Method;
import static org.apache.commons.lang.StringUtils.replace;

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

		sb.append(getParamsSignature(meth.getParameterTypes()));// avoid clone
		sb.append(getSignature(meth.getReturnType()));
		return sb.toString();
	}

	public static String getParamsSignature(Class<?>[] params) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");

		for (int j = 0; j < params.length; j++) {
			sb.append(getSignature(params[j]));
		}
		sb.append(")");
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
		// not correct, use for method in assertion generation
//		return "L" + className.replace('.', '/') + ";";
//		int i = className.lastIndexOf('.');
//		if (i != -1) {
//			className = className.substring(i + 1);
//		}
		return "L" + className.replace('.', '/') + ";";
	}
		
	public static String extractMethodName(String methodNameOrSign) {
		int endNameIdx = methodNameOrSign.indexOf("(");
		if (endNameIdx < 0) {
			return methodNameOrSign;
		}
		String fullMethodName = methodNameOrSign.substring(0, endNameIdx);
		if (fullMethodName.contains(".")) {
			return fullMethodName.substring(fullMethodName.lastIndexOf("."),
					fullMethodName.length() - 1);
		}
		return fullMethodName;
	}
	
	public static String extractSignature(String methodNameAndSign) {
		int endNameIdx = methodNameAndSign.indexOf("(");
		if (endNameIdx > 1) {
			return methodNameAndSign.substring(endNameIdx);
		}
		return StringUtils.EMPTY;
	}
	
	public static String trimSignature(String typeSign) {
		return replace(typeSign, ";", "");
	}

	public static String createMethodNameSign(String methodName, String signature) {
		return new StringBuilder(methodName).append(signature).toString();
	}
	
	public static String createMethodNameSign(Method method) {
		return createMethodNameSign(method.getName(), getSignature(method));
	}
}
