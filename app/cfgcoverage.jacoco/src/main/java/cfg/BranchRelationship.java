/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg;

/**
 * @author LLT
 * the branchRelationship is at bytecode level, not sourcecode
 * will be removed soon. 
 */
@Deprecated
public enum BranchRelationship {
	FALSE,
	TRUE,
	TRUE_FALSE;
	
	public boolean isTrueBranch() {
		return this == BranchRelationship.TRUE;
	}
	
	public boolean isFalseBranch() {
		return this == BranchRelationship.FALSE;
	}

	public static BranchRelationship valueOf(boolean isFalseBranch) {
		if (isFalseBranch) {
			return BranchRelationship.FALSE;
		}
		return TRUE;
	}

	public static BranchRelationship merge(BranchRelationship curRelationship, BranchRelationship newRelationship) {
		if (curRelationship == null) {
			return newRelationship;
		}
		if (newRelationship == null) {
			return curRelationship;
		}
		if (curRelationship != newRelationship) {
			return TRUE_FALSE;
		}
		return newRelationship;
	}
}
