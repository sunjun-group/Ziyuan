/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.formula;

import java.util.List;

import sav.common.core.formula.utils.ExpressionVisitor;

/**
 * @author LLT 
 * replace CNF
 */
public class AndFormula extends ConjunctionFormula {
	
	public AndFormula(Formula left, Formula right) {
		super(left, right);
	}
	
	public AndFormula() {
		super();
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof AndFormula)) {
			return false;
		}
		
		AndFormula formula = (AndFormula) obj;
		List<Formula> formulas = formula.elements;
		
		if (formulas.size() == 0 && elements.size() == 0) {
			return true;
		}
		
		if (formulas.size() != 2 || elements.size() != 2) {
			return false;
		}
		boolean x = formulas.get(0).equals(elements.get(0));
		x = formulas.get(1).equals(elements.get(1));
		x = formulas.get(0).equals(elements.get(1));
		x = formulas.get(1).equals(elements.get(0));
		
		return (formulas.get(0).equals(elements.get(0)) && formulas.get(1).equals(elements.get(1))) ||
				(formulas.get(0).equals(elements.get(1)) && formulas.get(1).equals(elements.get(0)));
	}
}
