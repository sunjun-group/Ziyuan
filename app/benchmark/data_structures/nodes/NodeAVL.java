package programs.nodes;

public class NodeAVL {

	public int data;
	
	public NodeAVL left, right;
	
	public NodeAVL() {
		this.data = 0;
		this.left = this.right = null;
	}
 
    public NodeAVL(int data, NodeAVL left, NodeAVL right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }
	
}
