/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import java.util.Collection;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.utils.IMethodUtils;

/**
 * @author LLT
 *
 */
public class MethodNameFilter implements TargetMethodFilter {
	private static Logger log = LoggerFactory.getLogger(MethodNameFilter.class);
	private Collection<String> excludedMethods;
	
	public MethodNameFilter(Collection<String> excludedMethods) {
		this.excludedMethods = excludedMethods;
	}

	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration method) {
		String methodId = IMethodUtils.getMethodId(cu, method);
		if (excludedMethods.contains(methodId)) {
			log.debug("ignore method: {}", methodId);
			return false;
		}
		return true;
	}

}
