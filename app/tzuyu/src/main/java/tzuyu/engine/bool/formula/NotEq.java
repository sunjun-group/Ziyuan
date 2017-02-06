/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.Operator;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Prestate;

/**
 * @author LLT
 *
 */
public class NotEq<T> extends Eq<T> {

	public NotEq(FieldVar var, Box<T> value) {
		super(var, value);
		op = Operator.NE;
	}

	@Override
	public boolean evaluate(Object[] objects) {
		return !super.evaluate(objects);
	}
	
	@Override
	public boolean evaluate(Prestate state) {
		return !super.evaluate(state);
	}
	
	@Override
	public String toString() {
		return attribute.toString() + " != " + valueBox.getDisplayValue();
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (o instanceof NotEq<?>);
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
