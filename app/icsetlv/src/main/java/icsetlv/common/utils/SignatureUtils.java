/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import icsetlv.common.exception.IcsetlvRuntimeException;

import org.apache.commons.lang.StringUtils;

import japa.parser.ast.body.MethodDeclaration;

/**
 * @author LLT
 *
 */
public class SignatureUtils {
	private SignatureUtils() {
	}

	public static String getSignature(String classCanonicalName) {
		return "L" + classCanonicalName.replace('.', '/');
	}

	public static String getSignature(MethodDeclaration method) {
		/* for temporary */
		return method.getName();
	}

	public static String createMethodNameSign(String methodName, String signature) {
		return methodName + signature;
	}
	
	public static String extractMethodName(String methodSign) {
		int endNameIdx = methodSign.indexOf("(");
		if (endNameIdx < 0) {
			throw new IcsetlvRuntimeException(
					"Expected: method signature, Receive: " + methodSign);
		}
		String fullMethodName = methodSign.substring(0, endNameIdx);
		if (fullMethodName.contains(".")) {
			return fullMethodName.substring(fullMethodName.lastIndexOf("."),
					fullMethodName.length() - 1);
		}
		return fullMethodName;
	}
	
	public static String trimSignature(String typeSign) {
		return StringUtils.replace(typeSign, ";", "");
	}
}
