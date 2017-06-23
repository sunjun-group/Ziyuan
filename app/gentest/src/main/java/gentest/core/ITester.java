/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core;

import gentest.core.data.MethodCall;
import gentest.core.data.Sequence;

import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public interface ITester {

	Pair<List<Sequence>, List<Sequence>> test(List<MethodCall> methodcalls)
			throws SavException;

}
