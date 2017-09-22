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
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.plugin.handler.filter.classfilter.ITypeFilter;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class GentestTypeFilter implements ITypeFilter {

	@Override
	public boolean isValid(CompilationUnit cu) {
		if (cu.types().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid(TypeDeclaration node) {
		if (node.isInterface() || ((node.isLocalTypeDeclaration() || node.isMemberTypeDeclaration())
				&& !Modifier.isPublic(node.getModifiers()))) {
			return false;
		}
		if (!node.isPackageMemberTypeDeclaration() && !Modifier.isPublic(node.getModifiers())) {
			return false;
		}
		return true;
	}

	public static List<ITypeFilter> createFilters() {
		return CollectionUtils.listOf(new GentestTypeFilter(), 1);
	}

}
