/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.formula;

import java.util.ArrayList;
import java.util.List;

import mosek.Env.branchdir;
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
		List<Formula> formulas = new ArrayList<>();
		formulas.addAll(formula.elements);
		
		if (formulas.size() !=  elements.size()) {
			return false;
		}
		
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			int j =0;
			boolean found = false;
			for (; j < formulas.size(); j++) {
				if (formulas.get(j).equals(elements.get(i))) {
					found = true;
					break;
				}
			}
			if (found) {
				formulas.remove(j);
			}else {
				return false;
			}
		}
		
		return true;
	}
}
