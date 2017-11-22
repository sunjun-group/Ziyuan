/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class SolutionBreakpointValue extends BreakpointValue {
	private int solutionIdx;
	
	public SolutionBreakpointValue(int solutionIdx) {
		super(StringUtils.lowLineJoin("solution", solutionIdx));
		this.solutionIdx = solutionIdx;
	}

	public int getSolutionIdx() {
		return solutionIdx;
	}
}
