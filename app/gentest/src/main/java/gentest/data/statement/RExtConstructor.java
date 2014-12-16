/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.data.statement;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author LLT
 *
 */
public class RExtConstructor extends RConstructor {
	private List<Statement> componentStmts;
	
	public RExtConstructor(Constructor<?> ctor) {
		super(ctor);
	}
	
}
