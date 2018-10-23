/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.plugin.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.activelearning.core.data.ClassInfo;
import learntest.activelearning.core.data.MethodInfo;
import sav.common.core.Constants;
import sav.common.core.Pair;
import sav.common.core.SavException;
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
		return ClassUtils.toClassMethodStr(PluginUtils.getFullNameOfCompilationUnit(cu),
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
	
	public static MethodInfo initTargetMethod(ActiveLearnTestConfig config) throws SavException, JavaModelException {
		ClassInfo targetClass = new ClassInfo(config.getTargetClassName());
		MethodInfo method = new MethodInfo(targetClass);
		method.setMethodName(config.getTargetMethodName());
		method.setLineNum(config.getMethodLineNumber());
		MethodDeclaration methodDeclaration = PluginUtils.findSpecificMethod(config.getProjectName(),
				method.getClassName(), method.getMethodName(), method.getLineNum());
		method.setMethodSignature(PluginUtils.getMethodSignature(methodDeclaration));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(methodDeclaration.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for (Object obj : methodDeclaration.parameters()) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		method.setParams(paramNames);
		method.setParamTypes(paramTypes);
		return method;
	}
}

