package learntest.core.commons.data.decision;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;

public enum BranchType {
	FALSE,
	TRUE,
	MORE;
	
	public boolean isTrueBranch() {
		return this == BranchType.TRUE;
	}
	
	public boolean isFalseBranch() {
		return this == BranchType.FALSE;
	}
	
	public BranchRelationship toBranchRelationship() {
		if (this == BranchType.FALSE) {
			return BranchRelationship.FALSE;
		} else if (this == TRUE) {
			return BranchRelationship.TRUE;
		}
		return BranchRelationship.TRUE_FALSE;
	}
}
