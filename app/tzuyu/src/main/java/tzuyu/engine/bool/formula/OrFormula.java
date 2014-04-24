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
 *
 */
public class OrFormula extends ConjunctionFormula {
	
	/**
	 * @deprecated 
	 * try {@link FormulaUtils#orOf(Formula, Formula)}
	 */
	@Deprecated
	public OrFormula() {
		super();
	}
	
	/**
	 * @deprecated 
	 * try {@link FormulaUtils#orOf(Formula, Formula)}
	 */
	@Deprecated
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
	public Operator getOperator() {
		return Operator.OR;
	}

	@Override
	public ConjunctionFormula createNew() {
		return new OrFormula();
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
