/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.formula;

import sav.common.core.formula.utils.ExpressionVisitor;


/**
 * @author LLT
 *
 */
public class Eq<T> extends VarAtom {
	private T value;
	
	public Eq(Var var, T value) {
		super(var, Operator.EQ);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this); 
	}
}
