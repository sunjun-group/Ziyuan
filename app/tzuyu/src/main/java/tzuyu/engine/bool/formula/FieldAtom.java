/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.bool.formula;

import tzuyu.engine.bool.FieldVar;


/**
 * @author LLT
 *
 */
public abstract class FieldAtom extends Atom{
	protected FieldVar attribute;
	
	public FieldAtom(FieldVar var) {
		this.attribute = var;
	}

	public FieldVar getKey() {
		return attribute;
	}
}
