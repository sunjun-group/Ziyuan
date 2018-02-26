/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;


/**
 * @author LLT
 *
 */
public abstract class AbstractMethodFilter extends ASTVisitor implements IMethodFilter {
	protected boolean isValid;
	List<String> ok = new LinkedList<>();
	List<String> invalid = new LinkedList<>();
	
	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		reset();
		md.accept(this);
		if (isValid) {
			ok.add(md.getName().toString());
		}else {
			invalid.add(md.getName().toString());
		}
		return isValid;
	}

	protected void reset() {
		isValid = true;
	}

	protected boolean getResult() {
		return isValid;
	}

	public boolean isValid() {
		return isValid;
	}

	public List<String> getOk() {
		return ok;
	}

	public List<String> getInvalid() {
		return invalid;
	}
}
