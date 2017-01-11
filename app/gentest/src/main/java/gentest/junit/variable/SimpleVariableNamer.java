/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit.variable;

import gentest.core.data.Sequence;

/**
 * @author LLT
 *
 */
public class SimpleVariableNamer implements IVariableNamer {
	private static final String VAR_PREFIX = "var";
	
	@Override
	public String getName(Class<?> type, int varId) {
		return getVar(varId);
	}

	private String getVar(int varId) {
		return VAR_PREFIX + varId;
	}

	@Override
	public String getArrVarName(int varId) {
		return getVar(varId);
	}

	@Override
	public String getExistVarName(int varId) {
		return getVar(varId);
	}

	@Override
	public void reset(Sequence method) {
		// do nothing
	}
	
}
