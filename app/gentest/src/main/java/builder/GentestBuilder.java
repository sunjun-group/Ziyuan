/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package builder;

import gentest.data.Sequence;

import java.util.List;

import sav.common.core.Logger;
import sav.common.core.Pair;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public abstract class GentestBuilder {
	protected Logger<?> logger = Logger.getDefaultLogger();

	public abstract Pair<List<Sequence>, List<Sequence>> generate()
			throws SavException;
}
