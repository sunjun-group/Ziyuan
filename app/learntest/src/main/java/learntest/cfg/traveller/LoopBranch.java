package learntest.cfg.traveller;

public class LoopBranch extends Branch {
	int lineNum;
	
	/**
	 * once or multiple times
	 */
	boolean isOnce;
	
	public LoopBranch(int lineNum, boolean isOnce) {
		super();
		this.lineNum = lineNum;
		this.isOnce = isOnce;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof LoopBranch){
			LoopBranch otherBranch = (LoopBranch)obj;
			if(this.lineNum==otherBranch.lineNum && this.isOnce==otherBranch.isOnce){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "LoopBranch [lineNum=" + lineNum + ", isOnce=" + isOnce + "]";
	}
	
	
}
