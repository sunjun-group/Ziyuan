/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.data.decision;

import cfgcoverage.jacoco.analysis.data.BranchCoveredType;
import learntest.testcase.data.BranchType;
import sav.common.core.utils.Assert;

/**
 * @author LLT
 *
 */
public enum CoveredBranches {
	TRUE (BranchCoveredType.TRUE),
	FALSE(BranchCoveredType.FALSE),
	TRUE_AND_FALSE(BranchCoveredType.TRUE_AND_FALSE),
	NONE(BranchCoveredType.NONE);
	
	private BranchCoveredType type;
	private CoveredBranches(BranchCoveredType type) {
		this.type = type;
	}
	
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

	public static CoveredBranches valueOf(BranchCoveredType branchCoveredType) {
		for (CoveredBranches type : values()) {
			if (type.type == branchCoveredType) {
				return type;
			}
		}
		return null;
	}
	
	public BranchCoveredType getType() {
		return type;
	}
}
