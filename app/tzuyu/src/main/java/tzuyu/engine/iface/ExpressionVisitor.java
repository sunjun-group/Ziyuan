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
import tzuyu.engine.bool.formula.AndFormula;
import tzuyu.engine.bool.formula.Atom;
import tzuyu.engine.bool.formula.ConjunctionFormula;
import tzuyu.engine.bool.formula.Eq;
import tzuyu.engine.bool.formula.False;
import tzuyu.engine.bool.formula.FieldAtom;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotEq;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.bool.formula.OrFormula;
import tzuyu.engine.bool.formula.True;

/**
 * @author LLT
 *
 */
public abstract class ExpressionVisitor {

	public void visit(NotFormula notFormula) {
		// do nothing by default 		
	}

	public void visit(True atom) {
		visitAtom(atom);
	}

	public void visit(False atom) {
		visitAtom(atom);
	}

	public void visit(LIAAtom liaAtom) {
		visitAtom(liaAtom);
	}
	
	public void visit(LIATerm liaTerm) {
		// do nothing by default		
	}

	public void visit(FieldVar fieldVar) {
		// do nothing by default
	}
	
	public void visit(AndFormula and) {
		visitConjunctionFormula(and);
	}
	
	public void visit(OrFormula or) {
		visitConjunctionFormula(or);
	}

	public <T> void visit(Eq<T> eq) {
		visitFieldAtom(eq);
	}
	
	public <T> void visit(NotEq<T> ne) {
		visitFieldAtom(ne);
	}

	/**
	 * this part is for abstract formula, 
	 * if these abstract formula are visited (return true), their subClass will not be visited.
	 */
	public void visitConjunctionFormula(ConjunctionFormula conj) {
		// do nothing by default
	}

	public void visitFieldAtom(FieldAtom atom) {
		visitAtom(atom);
	}
	
	public void visitAtom(Atom atom) {
		// do nothing by default
	}
}
