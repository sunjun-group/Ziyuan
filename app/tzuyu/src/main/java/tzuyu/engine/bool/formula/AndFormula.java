/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import java.util.List;

import tzuyu.engine.bool.Operator;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

/**
 * @author LLT 
 * to replace CNF
 */
public class AndFormula extends ConjunctionFormula {
	
	public AndFormula(Formula left, Formula right) {
		super(left, right);
	}
	
	public AndFormula() {
		super();
	}

	@Override
	public boolean evaluate(Object[] objects) {
		for (Formula clause : elements) {
			if (!clause.evaluate(objects)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean evaluate(Prestate state) {
		for (Formula clause : elements) {
			if (!clause.evaluate(state)) {
				return false;
			}
		}
		return true;
	}

	/* TODO-LLT: what different between restrict and simplify?*/
	@Override
	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		AndFormula result = new AndFormula();
		for (Formula clause : elements) {
			Formula expr = clause.restrict(vars, vals);
			if (expr instanceof False) {
				return Formula.FALSE;
			} else if (!(expr instanceof True)) {
				result.add(expr);
			}
		}

		if (result.elements.size() == 0) {
			return Formula.TRUE;
		}
		
		if (result.elements.size() == 1) {
			return result.elements.get(0);
		}
		
		return result;
	}

	@Override
	public Formula simplify() {
		AndFormula result = new AndFormula();
		for (Formula clause : elements) {
			Formula expr = clause.simplify();
			if (expr instanceof False) {
				return Formula.FALSE;
			} else if (!(expr instanceof True)) {
				result.add(expr);
			}
		}

		if (result.elements.size() == 0) {
			return Formula.TRUE;
		}
		
		if (result.elements.size() == 1) {
			return result.elements.get(0);
		}
		
		return result;
	}
	
	@Override
	protected ConjunctionFormula createNew() {
		return new AndFormula();
	}

	@Override
	public Operator getOperator() {
		return Operator.AND;
	}
	
}
