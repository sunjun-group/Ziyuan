/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package refiner.bool;

import tzuyu.engine.bool.Atom;

/**
 * @author LLT
 *
 */
public abstract class FieldAtom extends Atom{
	protected FieldVar variable;
	
	public FieldAtom(FieldVar var) {
		this.variable = var;
	}

	public FieldVar getKey() {
		return variable;
	}
}
