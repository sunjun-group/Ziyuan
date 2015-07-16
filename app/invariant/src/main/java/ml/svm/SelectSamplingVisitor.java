/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package ml.svm;

import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.utils.ExpressionVisitor;

/**
 * @author LLT
 *
 */
public class SelectSamplingVisitor extends ExpressionVisitor {

	@Override
	public void visit(LIAAtom liaAtom) {
		super.visit(liaAtom);
	}
	
}
