/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.utils;

import tzuyu.engine.bool.Operator;
import tzuyu.engine.bool.formula.AndFormula;
import tzuyu.engine.bool.formula.ConjunctionFormula;
import tzuyu.engine.bool.formula.Eq;
import tzuyu.engine.bool.formula.False;
import tzuyu.engine.bool.formula.LIAAtom;
import tzuyu.engine.bool.formula.NotEq;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.bool.formula.OrFormula;
import tzuyu.engine.bool.formula.True;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.utils.Pair;

/**
 * @author LLT
 *
 */
public class FormulaNegation extends ExpressionVisitor {
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
	public void visit(AndFormula and) {
		notFormula = new OrFormula();
		visitConjunctionFormula(and);
	}
	
	@Override
	public void visit(OrFormula or) {
		notFormula = new AndFormula();
		visitConjunctionFormula(or);
	}
	
	@Override
	public void visitConjunctionFormula(ConjunctionFormula cond) {
		for (Formula ele : cond.getElements()) {
			((ConjunctionFormula)notFormula).add(not(ele));
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
		return Operator.notOf(op);
	}
	
	public Formula getNotFormula() {
		return notFormula;
	}
	
	public static Formula not(Formula cond) {
		FormulaNegation visitor = new FormulaNegation();
		cond.accept(visitor);
		if (visitor.getNotFormula() == null) {
			return new NotFormula(cond);
		}
		return visitor.getNotFormula();
	}
}
