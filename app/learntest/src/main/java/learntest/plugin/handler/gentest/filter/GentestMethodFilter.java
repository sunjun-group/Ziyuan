/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest.filter;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

import learntest.plugin.handler.filter.methodfilter.IMethodFilter;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GentestMethodFilter implements IMethodFilter {

	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		/* invalid method: constructor, invisible method, or empty method */
		if (md.isConstructor() || !Modifier.isPublic(md.getModifiers()) || Modifier.isAbstract(md.getModifiers())) {
			return false;
		}
		if (CollectionUtils.isEmpty(md.getBody().statements())) {
			return false;
		}
		return true;
	}

	public static List<IMethodFilter> createFilters() {
		return CollectionUtils.listOf(new GentestMethodFilter(), 1);
	}
}
