/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package junit.variable;

import gentest.data.Sequence;

/**
 * @author LLT
 *
 */
public interface IVariableNamer {

	String getName(Class<?> type, int varId);

	String getArrVarName(int varId);

	String getExistVarName(int varIdx);

	void reset(Sequence method);
	
}
