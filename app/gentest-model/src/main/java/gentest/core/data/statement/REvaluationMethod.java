/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.statement;

import java.lang.reflect.Method;

/**
 * @author LLT
 *
 */
public class REvaluationMethod extends Rmethod {
	private static final long serialVersionUID = 6705370304237605630L;

	public REvaluationMethod(Method staticMethod) {
		super(staticMethod);
	}

	@Override
	public boolean accept(StatementVisitor visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public RStatementKind getKind() {
		return RStatementKind.EVALUATION_METHOD;
	}
}
