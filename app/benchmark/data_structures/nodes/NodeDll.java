package programs.nodes;

public class NodeDll {
	
	public int data;
	
	public NodeDll next;
	
	public NodeDll prev;
	
	public NodeDll(int data, NodeDll next, NodeDll prev) {
		this.data = data;
		this.next = next;
		this.prev = prev;
	}
	
	public NodeDll() {
		this.data = 0;
		this.next = null;
		this.prev = null;
	}

}
