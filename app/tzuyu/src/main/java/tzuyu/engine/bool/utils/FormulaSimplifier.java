/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.utils;

import tzuyu.engine.bool.formula.AndFormula;
import tzuyu.engine.bool.formula.NotFormula;
import tzuyu.engine.bool.formula.OrFormula;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.utils.ObjectUtils;

/**
 * @author LLT
 *
 */
@SuppressWarnings("deprecation")
public class FormulaSimplifier extends ExpressionVisitor {
	private Formula result;
	
	@Override
	public void visit(OrFormula or) {
		OrFormula newOr = new OrFormula();
		for (Formula term : or.getElements()) {
			Formula expr = simplify(term);
			if (Formula.TRUE.equals(expr)) {
				result = Formula.TRUE;
				return;
			} else if (!Formula.FALSE.equals(expr)) {
				newOr.add(expr);
			}
		}

		if (newOr.getElements().size() == 0) {
			result = Formula.FALSE;
		} else if (newOr.getElements().size() == 1) {
			result = newOr.getElements().get(0);
		} else {
			result = newOr;
		}
	}

	@Override
	public void visit(AndFormula and) {
		AndFormula newAnd = new AndFormula();
		for (Formula clause : and.getElements()) {
			Formula expr = simplify(clause);
			if (Formula.FALSE.equals(expr)) {
				result = Formula.FALSE;
				return;
			} else if (!Formula.TRUE.equals(expr)) {
				newAnd.add(expr);
			}
		}

		if (newAnd.getElements().size() == 0) {
			result = Formula.TRUE;
		} else if (newAnd.getElements().size() == 1) {
			result = newAnd.getElements().get(0);
		} else {
			result = newAnd;
		}
	}
	
	@Override
	public void visit(NotFormula notFormula) {
		result = FormulaUtils.notOf(simplify(notFormula.getChild()));
	}
	
	public static Formula simplify(Formula formula) {
		FormulaSimplifier visitor = new FormulaSimplifier();
		formula.accept(visitor);
		return ObjectUtils.returnValueOrAlt(visitor.result, formula);
	}
}
