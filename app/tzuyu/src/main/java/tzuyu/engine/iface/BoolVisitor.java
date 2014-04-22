/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.LIATerm;
import tzuyu.engine.bool.formula.ConjunctionFormula;
import tzuyu.engine.bool.formula.Eq;
import tzuyu.engine.bool.formula.False;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotEq;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.bool.formula.True;

/**
 * @author LLT
 *
 */
public abstract class BoolVisitor {

	public void visit(NotFormula notFormula) {
		// do nothing by default 		
	}

	public void visit(True formula) {
		// do nothing by default 		
	}

	public void visit(False false1) {
		// do nothing by default 		
	}

	public void visit(LIAAtom liaAtom) {
		// do nothing by default		
	}
	
	public void visit(LIATerm liaTerm) {
		// do nothing by default
	}

	public void visit(FieldVar fieldVar) {
		// do nothing by default
	}

	public <T>void visit(Eq<T> formula) {
		// do nothing by default
	}

	public <T>void visit(NotEq<T> formula) {
		// TODO Auto-generated method stub
	}
	
	public void visit(ConjunctionFormula conjunctionExtFormula) {
		// TODO Auto-generated method stub
		
	}
}
