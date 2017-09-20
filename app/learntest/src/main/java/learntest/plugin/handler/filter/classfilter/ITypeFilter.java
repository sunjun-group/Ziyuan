/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.classfilter;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * @author LLT
 *
 */
public interface ITypeFilter {

	public boolean isValid(CompilationUnit cu);

	public boolean isValid(TypeDeclaration typeDecl);
}
