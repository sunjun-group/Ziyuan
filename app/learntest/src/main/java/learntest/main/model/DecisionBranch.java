package learntest.main.model;


public class DecisionBranch extends Branch {
	int lineNum;
	boolean isTrue;
	
	public DecisionBranch(int lineNum, boolean isTrue) {
		super();
		this.lineNum = lineNum;
		this.isTrue = isTrue;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof DecisionBranch){
			DecisionBranch otherBranch = (DecisionBranch)obj;
			if(this.lineNum==otherBranch.lineNum && this.isTrue==otherBranch.isTrue){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "DecisionBranch [lineNum=" + lineNum + ", isTrue=" + isTrue + "]";
	}
	
	
}
