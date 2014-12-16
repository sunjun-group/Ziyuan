/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package junit;

import gentest.data.Sequence;

import java.util.List;

import sav.common.core.Pair;

/**
 * @author LLT
 *
 */
public interface ITestsPrinter {

	void printTests(Pair<List<Sequence>, List<Sequence>> testSeqss);

}
