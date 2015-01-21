/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core;

import gentest.core.data.Sequence;

/**
 * @author LLT
 *
 */
public abstract class GentestListener {

	public void onSetOutVarId(String alias, int outVarId) {
		// Do nothing by default
	}

	public void onFinishGenerateSeq(TestcaseGenerator tcGenerator,
			Sequence testcase) {
		// Do nothing by default
	}
	
}
