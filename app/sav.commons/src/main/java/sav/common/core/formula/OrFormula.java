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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof OrFormula)) {
			return false;
		}
		
		OrFormula formula = (OrFormula) obj;
		List<Formula> formulas = formula.elements;
		
		if (formulas.size() !=  elements.size()) {
			return false;
		}
		
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			if (!formulas.get(i).equals(elements.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
