/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import learntest.util.LearnTestUtil;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class MethodNameFilter implements TargetMethodFilter {
	private Collection<String> excludedMethods;
	
	public MethodNameFilter(Collection<String> excludedMethods) {
		this.excludedMethods = excludedMethods;
	}

	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration method) {
		String methodName = ClassUtils.toClassMethodStr(LearnTestUtil.getFullNameOfCompilationUnit(cu),
				method.getName().getIdentifier());
		int startLine = cu.getLineNumber(method.getStartPosition());
		return !excludedMethods.contains(toMethodId(methodName, startLine));
	}

	public static String toMethodId(String methodName, int methodStartLine) {
		return StringUtils.dotJoin(methodName, methodStartLine);
	}
	

}
