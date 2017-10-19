package programs.nodes;

public class NodeBST {
	
	public int data;
	
	public NodeBST left;
	
	public NodeBST right;
	
	public NodeBST() {
		this.data = 0;
		this.left = this.right = null;
	}
	
	public NodeBST(int data, NodeBST left, NodeBST right) {
		this.data = data;
		this.left = left;
		this.right = right;
	}

}
