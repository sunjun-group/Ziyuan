/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool;

import tzuyu.engine.bool.formula.AndFormula;
import tzuyu.engine.bool.formula.ConjunctionFormula;
import tzuyu.engine.bool.formula.Eq;
import tzuyu.engine.bool.formula.False;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotEq;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.bool.formula.OrFormula;
import tzuyu.engine.bool.formula.True;
import tzuyu.engine.iface.BoolVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.utils.Pair;

/**
 * @author LLT
 *
 */
public class FormulaNegation extends BoolVisitor {
	private Formula notFormula;
	
	@Override
	public <T> void visit(Eq<T> formula) {
		notFormula = new NotEq<T>(formula.getKey(), formula.getValueBox());
	}
	
	@Override
	public <T> void visit(NotEq<T> formula) {
		notFormula = new Eq<T>(formula.getKey(), formula.getValueBox());
	}
	
	@Override
	public void visit(ConjunctionFormula cond) {
		if (cond.getOperator() == Operator.AND) {
			notFormula = new OrFormula();
		} else {
			notFormula = new AndFormula();
		}
		for (Formula ele : cond.getElements()) {
			((ConjunctionFormula)notFormula).add(notOf(ele));
		}
	}
	
	@Override
	public void visit(False cond) {
		notFormula = True.getInstance();
	}
	
	@Override
	public void visit(True cond) {
		notFormula = False.getInstance();
	}
	
	@Override
	public void visit(LIAAtom liaAtom) {
		notFormula = new LIAAtom(liaAtom.getMVFOExpr(),
				notOf(liaAtom.getOperator()), liaAtom.getConstant());
	}
	
	private Operator notOf(Operator op) {
		for (Pair<Operator, Operator> pair : Operator.OPPOSITE_PAIRS) {
			if (op == pair.a) {
				return pair.b;
			}
			if (op == pair.b) {
				return pair.a;
			}
		}
		return null;
	}
	
	public Formula getNotFormula() {
		return notFormula;
	}
	
	public static Formula notOf(Formula cond) {
		FormulaNegation visitor = new FormulaNegation();
		cond.accept(visitor);
		if (visitor.getNotFormula() == null) {
			return new NotFormula(cond);
		}
		return visitor.getNotFormula();
	}
}
