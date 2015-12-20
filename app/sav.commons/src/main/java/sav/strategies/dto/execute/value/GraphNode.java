package sav.strategies.dto.execute.value;

import java.util.List;

public interface GraphNode {
	public List<? extends GraphNode> getChildren();
	public GraphNode getParent();
	
	/**
	 * This method compares the labels of two nodes. It will not further compare their children.
	 * @param node
	 * @return
	 */
	public boolean match(GraphNode node);
	
	public boolean isVisited();
	public void setVisited(boolean isVisited);
	
	public boolean isTheSameWith(GraphNode nodeAfter);
}
