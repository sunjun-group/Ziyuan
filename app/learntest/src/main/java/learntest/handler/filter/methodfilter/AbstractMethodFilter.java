/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.handler.filter.methodfilter;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * @author LLT
 *
 */
public abstract class AbstractMethodFilter extends ASTVisitor implements TargetMethodFilter {
	protected boolean isValid;
	
	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		reset();
		md.accept(this);
		return isValid;
	}

	protected void reset() {
		isValid = true;
	}

	protected boolean getResult() {
		return isValid;
	}
}
