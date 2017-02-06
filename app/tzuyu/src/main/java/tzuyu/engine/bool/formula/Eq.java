/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.ObjectUtils;
import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.Operator;
import tzuyu.engine.bool.Var;
import tzuyu.engine.iface.ExpressionVisitor;
import tzuyu.engine.model.Prestate;

/**
 * @author LLT
 *
 */
public class Eq<T> extends FieldAtom {
	protected Box<T> valueBox;

	public Eq(FieldVar var, Box<T> value) {
		super(var, Operator.EQ);
		this.valueBox = value;
	}
	
	@Override
	public List<Var> getReferencedVariables() {
		List<Var> vars = new ArrayList<Var>();
		vars.add(attribute);
		return vars;
	}

	@Override
	public boolean evaluate(Object[] objects) {
		Object obj = attribute.getValue(objects);
		return ObjectUtils.equalsWithNull(obj, valueBox.getValue());
	}

	@Override
	public boolean evaluate(Prestate state) {
		return valueBox.evaluate(attribute.getObjectInfo(state));
	}
	
	public T getValue() {
		return valueBox.getValue();
	}

	public Box<T> getValueBox() {
		return valueBox;
	}
	
	@Override
	public String toString() {
		return attribute.toString() + " == " + valueBox.getDisplayValue();
	}

	@Override
	public int hashCode() {
		int hashcode = op.hashCode() * 17 + attribute.hashCode() * 31;
		if (valueBox.getValue() != null) {
			hashcode += valueBox.getValue().hashCode();
		}
		return hashcode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Eq<?>)) {
			return false;
		}

		Eq<?> oEq = (Eq<?>) o;

		return this.attribute.equals(oEq.attribute)
				&& ObjectUtils.equalsWithNull(valueBox.getValue(), oEq
						.getValueBox().getValue());
	}

	@Override
	public String getDisplayValue() {
		return valueBox.getDisplayValue();
	}
	
	@Override
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}
}
