/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.data;

import icsetlv.common.dto.BreakpointValue;

/**
 * @author LLT
 *
 */
public class SolutionBreakpointValue extends BreakpointValue {
	private int solutionIdx;
	
	public SolutionBreakpointValue(int solutionIdx) {
		super();
		this.solutionIdx = solutionIdx;
	}

	public int getSolutionIdx() {
		return solutionIdx;
	}
	
}
