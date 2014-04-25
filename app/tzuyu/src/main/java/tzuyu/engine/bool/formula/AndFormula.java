/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import tzuyu.engine.bool.Operator;
import tzuyu.engine.bool.utils.FormulaUtils;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Prestate;

/**
 * @author LLT 
 * replace CNF
 */
public class AndFormula extends ConjunctionFormula {
	
	/**
	 * @deprecated use {@link FormulaUtils#and(Formula, Formula)} instead
	 */
	@Deprecated
	public AndFormula(Formula left, Formula right) {
		super(left, right);
	}
	
	/**
	 * @deprecated use {@link FormulaUtils#and(Formula, Formula)} instead
	 */
	@Deprecated
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
	
	@Override
	public ConjunctionFormula createNew() {
		return new AndFormula();
	}

	@Override
	public Operator getOperator() {
		return Operator.AND;
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
