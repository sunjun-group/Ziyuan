package microbat.graphdiff;

import sav.strategies.dto.execute.value.GraphNode;

public class GraphDiff {
	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final String UPDATE = "update";
	
	private GraphNode nodeBefore;
	private GraphNode nodeAfter;
	
	public GraphDiff(GraphNode nodeBefore, GraphNode nodeAfter) {
		super();
		
		if(nodeBefore == null && nodeAfter == null){
			System.err.println("both before-node and after-node are empty!");
		}
			
		this.nodeBefore = nodeBefore;
		this.nodeAfter = nodeAfter;
		
	}

	public String getDiffType(){
		if(this.nodeBefore == null && this.nodeAfter != null){
			return GraphDiff.ADD;
		}
		else if(this.nodeBefore != null && this.nodeAfter == null){
			return GraphDiff.REMOVE;
		}
		else if(this.nodeBefore != null && this.nodeAfter != null){
			return GraphDiff.UPDATE;
		}
		else{
			System.err.println("both before-node and after-node are empty for a change!");
			return null;
		}
	}
}
