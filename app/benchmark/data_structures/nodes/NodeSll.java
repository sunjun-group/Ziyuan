package programs.nodes;
public class NodeSll {

	public int data;
		
	public NodeSll next;

	public NodeSll() {
		this.data = 0;
		this.next = null;
	}
	
	public NodeSll(int data, NodeSll next) {
		this.data = data;
		this.next = next;
	}
	
}
