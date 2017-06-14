/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * @author LLT
 *
 */
public class TestableMethodFilter implements TargetMethodFilter {

	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		if(md.isConstructor() || md.parameters().isEmpty()
				|| !Modifier.isPublic(md.getModifiers())){
			return false;
		}
		return true;
	}

}
