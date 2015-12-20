package microbat.graphdiff;

import java.util.ArrayList;
import java.util.List;

import sav.strategies.dto.execute.value.GraphNode;

public class HierarchyGraphDiffer {
	
	class MatchingGraphPair{
		private GraphNode nodeBefore;
		private GraphNode nodeAfter;
		
//		private boolean isDiff;
		
		public MatchingGraphPair(GraphNode nodeBefore, GraphNode nodeAfter) {
			super();
			this.nodeBefore = nodeBefore;
			this.nodeAfter = nodeAfter;
//			this.isDiff = isDiff;
		}
		public GraphNode getNodeBefore() {
			return nodeBefore;
		}
		public void setNodeBefore(GraphNode nodeBefore) {
			this.nodeBefore = nodeBefore;
		}
		public GraphNode getNodeAfter() {
			return nodeAfter;
		}
		public void setNodeAfter(GraphNode nodeAfter) {
			this.nodeAfter = nodeAfter;
		}
//		public boolean isDiff() {
//			return isDiff;
//		}
//		public void setDiff(boolean isDiff) {
//			this.isDiff = isDiff;
//		}
		
	}
	
	private List<GraphDiff> diffs = new ArrayList<>();
	
	public void diff(GraphNode rootBefore, GraphNode rootAfter){
		List<? extends GraphNode> childrenBefore = rootBefore.getChildren();
		List<? extends GraphNode> childrenAfter = rootAfter.getChildren();
		
		if(rootBefore == null && rootAfter == null){
			return;
		}
		
		List<MatchingGraphPair> pairs = matchList(childrenBefore, childrenAfter);
		
		for(MatchingGraphPair pair: pairs){
			GraphNode nodeBefore = pair.getNodeBefore();
			GraphNode nodeAfter = pair.getNodeAfter();
			
			if(nodeBefore != null && nodeAfter != null){
				if(!nodeBefore.isTheSameWith(nodeAfter)){
					GraphDiff diff = new GraphDiff(nodeBefore, nodeAfter);
					this.diffs.add(diff);
				}
				
				diff(nodeBefore, nodeAfter);
			}
			else{
				GraphDiff diff = new GraphDiff(nodeBefore, nodeAfter);
				this.diffs.add(diff);
			}
		}
	}

	private List<MatchingGraphPair> matchList(List<? extends GraphNode> childrenBefore,
			List<? extends GraphNode> childrenAfter) {
		List<MatchingGraphPair> pairs = new ArrayList<>();
		
		for(GraphNode childBefore: childrenBefore){
			if(!childBefore.isVisited()){
				/**
				 * find a matchable node in <code>childrenAfter</code>
				 */
				GraphNode node = null;
				for(GraphNode childAfter: childrenAfter){
					if(!childAfter.isVisited() && childBefore.match(childAfter)){
						node = childAfter;
						break;
					}
				}
				System.currentTimeMillis();
				
				childBefore.setVisited(true);
				if(node != null){
					node.setVisited(true);
				}
				MatchingGraphPair pair = new MatchingGraphPair(childBefore, node);
				pairs.add(pair);
			}
		}
		
		
		for(GraphNode childAfter: childrenAfter){
			if(!childAfter.isVisited()){
				childAfter.setVisited(true);
				MatchingGraphPair pair = new MatchingGraphPair(null, childAfter);
				pairs.add(pair);
			}
		}
		
		return pairs;
	}
	
	public List<GraphDiff> getDiffs(){
		return this.diffs;
	}
}
