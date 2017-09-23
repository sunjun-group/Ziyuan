/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis.data;

/**
 * @author LLT
 *
 */
public enum BranchCoveredType {
	TRUE,
	FALSE,
	TRUE_AND_FALSE,
	NONE;

	public static BranchCoveredType append(BranchCoveredType type, BranchRelationship branchRelationship) {
		BranchCoveredType newCoveredType = valueOf(branchRelationship);
		return append(type, newCoveredType);
	}

	public static BranchCoveredType append(BranchCoveredType type, BranchCoveredType newCoveredType) {
		if (type == BranchCoveredType.NONE) {
			return newCoveredType;
		}
		if (newCoveredType == BranchCoveredType.NONE) {
			return type;
		}
		if (newCoveredType != type) {
			return TRUE_AND_FALSE;
		}
		return type;
	}

	public static BranchCoveredType valueOf(BranchRelationship branchRelationship) {
		switch (branchRelationship) {
		case TRUE:
			return TRUE;
		case FALSE:
		case TRUE_FALSE:
			return BranchCoveredType.FALSE;
		}
		return NONE;
	}
	
	
}
