/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.plugin.utils.filter;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * @author LLT
 *
 */
public class TestableClassFilter implements ITypeFilter {

	List<String> ok = new LinkedList<>();
	List<String> interfaces = new LinkedList<>();
	List<String> abstracts = new LinkedList<>();
	List<String> notPublicClasses = new LinkedList<>();
	
	public boolean isValid(CompilationUnit cu) {
		if (cu.types().isEmpty()) {
			return false;
		}
		AbstractTypeDeclaration type = (AbstractTypeDeclaration) cu.types().get(0);
		if (isInterfaceOrAbstractType(type)){
			return false;
		}
		else if (!Modifier.isPublic(type.getModifiers())) {
			notPublicClasses.add(type.getName().toString());
			return false;
		}
		ok.add(type.getName().toString());
		return true;
	}

	private boolean isInterfaceOrAbstractType(AbstractTypeDeclaration type) {
		if (!(type instanceof TypeDeclaration)) {
			return false;
		}
		TypeDeclaration td = (TypeDeclaration) type;
		if (td.isInterface()) {
			interfaces.add(type.getName().toString());
		}else if (Modifier.isAbstract(type.getModifiers())) {
			abstracts.add(type.getName().toString());
		}
		return td.isInterface() || Modifier.isAbstract(type.getModifiers());
	}

	@Override
	public boolean isValid(TypeDeclaration typeDecl) {
		return true;
	}

	public List<String> getOk() {
		return ok;
	}

	public List<String> getNotPublicClasses() {
		return notPublicClasses;
	}

	public List<String> getInterfaces() {
		return interfaces;
	}

	public List<String> getAbstracts() {
		return abstracts;
	}
	
	
}
