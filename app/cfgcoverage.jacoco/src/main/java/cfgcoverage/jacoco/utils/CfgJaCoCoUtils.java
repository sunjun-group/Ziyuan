/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.utils;

import org.objectweb.asm.tree.MethodNode;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class CfgJaCoCoUtils {
	private CfgJaCoCoUtils(){}
	
	public static String createMethodId(String className, MethodNode method) {
		String methodName = method.name;
		String methodSign = method.desc;
		return createMethodId(className, methodName, methodSign);
	}

	public static String createMethodId(String className, String methodName, String methodSign) {
		String fullMethodName = ClassUtils.toClassMethodStr(className, methodName);
		return SignatureUtils.createMethodNameSign(fullMethodName, methodSign);
	}
}
