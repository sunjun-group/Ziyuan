package programs.nodes;

public class NodeRB {
	
	public int data;
	
	public boolean isBlack;
    
	public NodeRB left, right, parent;
 
    public NodeRB() {
        this.data = 0;
        this.isBlack = true;
        this.left = this.right = this.parent = null;
    }
	
	public NodeRB(int data, boolean isBlack, NodeRB left, NodeRB right, NodeRB parent) {
		this.data = data;
		this.isBlack = isBlack;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

}
