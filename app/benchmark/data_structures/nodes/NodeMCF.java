package programs.nodes;

public class NodeMCF {

	public TreeMCF child;
	
	public NodeMCF prev;
	
	public NodeMCF next;
	
	public TreeMCF parent;
	
	public NodeMCF(TreeMCF child, NodeMCF prev, NodeMCF next, TreeMCF parent) {
		this.child = child;
		this.prev = prev;
		this.next = next;
		this.parent = parent;
	}
	
	public NodeMCF() {
		this.child = this.parent = null;
		this.prev = this.next = null;
	}

}
