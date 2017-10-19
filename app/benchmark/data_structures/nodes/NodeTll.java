package programs.nodes;

public class NodeTll {
	
	public NodeTll left;
	
	public NodeTll right;
	
	public NodeTll next;
	
	public NodeTll() {
		this.left = this.right = this.next = null;
	}
	
	public NodeTll(NodeTll left, NodeTll right, NodeTll next) {
		this.left = left;
		this.right = right;
		this.next = next;
	}

}
