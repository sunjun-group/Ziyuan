/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.utils;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import learntest.core.commons.data.classinfo.ClassInfo;
import learntest.core.commons.data.classinfo.MethodInfo;
import sav.common.core.Constants;
import sav.common.core.Pair;
import sav.common.core.utils.Assert;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.NumberUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class IMethodUtils {

	public static int getStartLineNo(CompilationUnit cu, MethodDeclaration method) {
		if (method.getBody() == null || method.getBody().statements().isEmpty()) {
			return cu.getLineNumber(method.getStartPosition());
		}
		return cu.getLineNumber(((ASTNode)method.getBody().statements().get(0)).getStartPosition());
	}
	
	public static int getLength(CompilationUnit cu, MethodDeclaration method) {
		if (method.getBody() == null || CollectionUtils.isEmpty(method.getBody().statements())) {
			return 0;
		}
		List<?> statements = method.getBody().statements();
		
		ASTNode firstStatement = (ASTNode) statements.get(0);
		ASTNode lastStatement = (ASTNode) statements.get(statements.size() - 1);
		int length = cu.getLineNumber(method.getStartPosition() + method.getLength()) - cu.getLineNumber(firstStatement.getStartPosition());
//		return cu.getLineNumber(lastStatement.getStartPosition())
//				- cu.getLineNumber(firstStatement.getStartPosition());
		return length;
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

	/**
	 * methodId: [className].[methodName].[lineNumber]
	 * */
	public static MethodInfo toMethodInfo(String methodId) {
		int idx = methodId.lastIndexOf(Constants.DOT);
		Assert.assertTrue(idx >= 0, "Invalid methodId: " + methodId);
		
		int lineNumber = NumberUtils.toNumber(methodId.substring(idx + 1), -1);
		Assert.assertTrue(lineNumber > 0,  "Invalid methodId: " + methodId);
		
		Pair<String, String> classMethod = ClassUtils.splitClassMethod(methodId.substring(0, idx));
		
		ClassInfo targetClass = new ClassInfo(classMethod.a);
		MethodInfo method = new MethodInfo(targetClass);
		method.setMethodName(classMethod.b);
		method.setLineNum(lineNumber);
		return method;
	}
}

