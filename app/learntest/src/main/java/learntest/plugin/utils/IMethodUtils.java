/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class IMethodUtils {

	public static int getStartLineNo(CompilationUnit cu, MethodDeclaration method) {
		return cu.getLineNumber(((ASTNode)method.getBody().statements().get(0)).getStartPosition());
	}
	
	public static int getLength(CompilationUnit cu, MethodDeclaration method) {
		if (method.getBody() == null || CollectionUtils.isEmpty(method.getBody().statements())) {
			return 0;
		}
		return cu.getLineNumber(method.getStartPosition() + method.getLength())
				- cu.getLineNumber(method.getStartPosition());
	}
	
	public static String getMethodId(CompilationUnit cu, MethodDeclaration method) {
		String methodName = getMethodFullName(cu, method);
		int startLine = IMethodUtils.getStartLineNo(cu, method);
		String methodId = getMethodId(methodName, startLine);
		return methodId;
	}

	public static String getMethodFullName(CompilationUnit cu, MethodDeclaration method) {
		return ClassUtils.toClassMethodStr(LearnTestUtil.getFullNameOfCompilationUnit(cu),
				method.getName().getIdentifier());
	}

	public static String getMethodId(String methodName, int methodStartLine) {
		return StringUtils.dotJoin(methodName, methodStartLine);
	}

}
