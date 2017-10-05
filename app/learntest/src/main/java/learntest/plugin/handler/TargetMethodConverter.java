/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.core.commons.data.classinfo.ClassInfo;
import learntest.plugin.commons.PluginException;
import learntest.plugin.utils.AstUtils;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class TargetMethodConverter {
	
	public static List<MethodInfo> toTargetMethods(CompilationUnit cu, List<MethodDeclaration> methodList) {
		if (CollectionUtils.isEmpty(methodList)) {
			return new ArrayList<MethodInfo>(0);
		}
		List<MethodInfo> targetMethods = new ArrayList<MethodInfo>(methodList.size());
		for (MethodDeclaration method : methodList) {
			targetMethods.add(toTargetMethod(cu, method));
		}
		return targetMethods;
	}

	public static MethodInfo toTargetMethod(IMethod imethod) throws PluginException {
		try {
			CompilationUnit cu = AstUtils.toAstNode(imethod.getCompilationUnit());
			MethodDeclaration method = (MethodDeclaration) NodeFinder.perform(cu, imethod.getSourceRange());
			return toTargetMethod(cu, method);
		} catch (JavaModelException e) {
			throw PluginException.wrapEx(e);
		}
	}

	public static MethodInfo toTargetMethod(CompilationUnit cu, MethodDeclaration method) {
		String simpleMethodName = method.getName().getIdentifier();
		String className =  getClassFullName(method);
		ClassInfo targetClass = new ClassInfo(className);
		MethodInfo targetMethod = new MethodInfo(targetClass);
		targetMethod.setMethodName(simpleMethodName);
		int lineNumber = IMethodUtils.getStartLineNo(cu, method);
		targetMethod.setLineNum(lineNumber);
		targetMethod.setMethodLength(IMethodUtils.getLength(cu, method));
		targetMethod.setMethodSignature(LearnTestUtil.getMethodSignature(method));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(method.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for(Object obj: method.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		targetMethod.setParams(paramNames);
		targetMethod.setParamTypes(paramTypes);
		return targetMethod;
	}

	public static String getClassFullName(MethodDeclaration method) {
		LinkedList<String> owner = new LinkedList<String>();
		String declaredType = null;
		String pkgName = null;
		ASTNode node = method;
		boolean stop = false;
		do {
			node = node.getParent();
			switch (node.getNodeType()) {
			case ASTNode.TYPE_DECLARATION:
				String typeName = ((TypeDeclaration) node).getName().getFullyQualifiedName();
				if (declaredType == null) {
					declaredType = typeName;
				} else {
					owner.addFirst(typeName);
				}
				break;
			case ASTNode.COMPILATION_UNIT:
				CompilationUnit cu = (CompilationUnit) node;
				pkgName = cu.getPackage().getName().getFullyQualifiedName();
				stop = true;
				break;
			}
		} while (!stop);
		
		return ClassUtils.getClassFullName(pkgName, declaredType, owner.toArray(new String[owner.size()]));
	}

	
}
