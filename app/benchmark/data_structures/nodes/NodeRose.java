package programs.nodes;

public class NodeRose {
	
	public TreeRose child;
	
	public NodeRose next;
	
	public TreeRose parent;
	
	public NodeRose(TreeRose child, NodeRose next, TreeRose parent) {
		this.child = child;
		this.next = next;
		this.parent = parent;
	}
	
	public NodeRose() {
		this.child = this.parent = null;
		this.next = null;
	}

}
