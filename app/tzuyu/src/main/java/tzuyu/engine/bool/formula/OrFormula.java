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
 *
 */
public class OrFormula extends ConjunctionFormula {
	
	public OrFormula() {
		super();
	}
	
	public OrFormula(Formula left, Formula right) {
		super(left, right);
	}

	@Override
	public boolean evaluate(Object[] objects) {
		for (Formula term : elements) {
			if (term.evaluate(objects)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean evaluate(Prestate state) {
		for (Formula term : elements) {
			if (term.evaluate(state)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Formula restrict(List<Atom> vars, List<Integer> vals) {
		OrFormula result = new OrFormula();
		for (Formula term : elements) {
			Formula expr = term.restrict(vars, vals);
			if (expr instanceof True) {
				return Formula.TRUE;
			} else if (!(expr instanceof False)) {
				result.add(expr);
			}
		}

		if (result.elements.size() == 0) {
			return Formula.FALSE;
		}
		
		if (result.elements.size() == 1) {
			return result.elements.get(0);
		}
		
		return result;
	}

	@Override
	public Formula simplify() {
		OrFormula result = new OrFormula();
		for (Formula term : elements) {
			Formula expr = term.simplify();
			if (expr instanceof True) {
				return Formula.TRUE;
			} else if (!(expr instanceof False)) {
				result.add(expr);
			}
		}

		if (result.elements.size() == 0) {
			return Formula.FALSE;
		}
		
		if (result.elements.size() == 1) {
			return result.elements.get(0);
		}
		
		return result;
	}

	@Override
	public Operator getOperator() {
		return Operator.OR;
	}

	@Override
	protected ConjunctionFormula createNew() {
		return new OrFormula();
	}
}
