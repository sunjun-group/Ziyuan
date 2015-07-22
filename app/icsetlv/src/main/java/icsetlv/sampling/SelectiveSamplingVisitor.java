/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import sav.common.core.formula.Atom;
import sav.common.core.formula.utils.ExpressionVisitor;

/**
 * @author LLT
 *
 */
public class SelectiveSamplingVisitor extends ExpressionVisitor {

	@Override
	public void visitAtom(Atom atom) {
		super.visitAtom(atom);
	}
}
