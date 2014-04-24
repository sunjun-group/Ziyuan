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


/**
 * @author LLT
 *
 */
public abstract class FieldAtom extends Atom {
	protected FieldVar attribute;
	protected Operator op;
	
	public FieldAtom(FieldVar var, Operator op) {
		this.attribute = var;
		this.op = op;
	}

	public FieldVar getKey() {
		return attribute;
	}
	
	public Operator getOperator() {
		return op;
	}

	public abstract String getDisplayValue();
}
