/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.classfilter;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * @author LLT
 *
 */
public class TestableClassFilter implements ITypeFilter {
	
	public boolean isValid(CompilationUnit cu) {
		if (cu.types().isEmpty()) {
			return false;
		}
		AbstractTypeDeclaration type = (AbstractTypeDeclaration) cu.types().get(0);
		if (isInterfaceOrAbstractType(type) || !Modifier.isPublic(type.getModifiers())) {
			return false;
		}
		return true;
	}

	private boolean isInterfaceOrAbstractType(AbstractTypeDeclaration type) {
		if (!(type instanceof TypeDeclaration)) {
			return false;
		}
		TypeDeclaration td = (TypeDeclaration) type;
		return td.isInterface() || Modifier.isAbstract(type.getModifiers());
	}

	@Override
	public boolean isValid(TypeDeclaration typeDecl) {
		return true;
	}
}
