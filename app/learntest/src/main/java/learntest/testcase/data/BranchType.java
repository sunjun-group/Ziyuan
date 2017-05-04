package learntest.testcase.data;

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
}
