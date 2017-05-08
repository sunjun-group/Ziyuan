/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import learntest.testcase.data.BranchType;
import sav.common.core.utils.Assert;

/**
 * @author LLT
 *
 */
public enum CoveredBranches {
	TRUE,
	FALSE,
	TRUE_AND_FALSE,
	NONE;
	
	public boolean onlyMissingTrue() {
		return this == TRUE;
	}

	public static CoveredBranches valueOf(boolean coveredTrue, boolean coveredFalse) {
		if (coveredTrue && coveredFalse) {
			return TRUE_AND_FALSE;
		}
		if (coveredTrue) {
			return TRUE;
		}
		if (coveredFalse) {
			return FALSE;
		}
		return NONE;
	}

	public BranchType getOnlyOneMissingBranch() {
		Assert.assertTrue(this != NONE, "both branches are not covered!");
		if (this == CoveredBranches.TRUE) {
			return BranchType.FALSE;
		}
		if (this == CoveredBranches.FALSE) {
			return BranchType.TRUE;
		}
		return null;
	}

	public boolean isOneBranchMissing() {
		return this == TRUE || this == FALSE;
	}

	public boolean coversTrue() {
		return this == TRUE || this == TRUE_AND_FALSE;
	}
}
