package microbat.evaluation.model;

import microbat.model.trace.TraceNode;

public class TraceNodePair {

	private TraceNode originalNode;
	private TraceNode mutatedNode;
	
	private boolean isExactlySame;
	
	public TraceNodePair(TraceNode mutatedNode, TraceNode originalNode) {
		this.originalNode = originalNode;
		this.mutatedNode = mutatedNode;
	}

	public TraceNode getOriginalNode() {
		return originalNode;
	}

	public void setOriginalNode(TraceNode originalNode) {
		this.originalNode = originalNode;
	}

	public TraceNode getMutatedNode() {
		return mutatedNode;
	}

	public void setMutatedNode(TraceNode mutatedNode) {
		this.mutatedNode = mutatedNode;
	}

	public void setExactSame(boolean b) {
		this.isExactlySame = b;
	}

	public boolean isExactSame(){
		return this.isExactlySame;
	}
	
	
}
