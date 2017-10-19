package programs.nodes;

public class NodeBST2 {
	
	public int data;
	
	public NodeBST2 left;
	
	public NodeBST2 right;
	
	public NodeBST2 parent;
	
	public NodeBST2() {
		this.data = 0;
		this.left = this.right = this.parent = null;
	}
	
	public NodeBST2(int data, NodeBST2 left, NodeBST2 right, NodeBST2 parent) {
		this.data = data;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

}
