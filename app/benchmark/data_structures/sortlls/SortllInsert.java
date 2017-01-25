package programs.sortlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SortllTemplate;

public class SortllInsert {

    private NodeSll insert(NodeSll x, int i) {
        if (x == null)
            return new NodeSll(i, null);
        else if (x.data > i) {
            NodeSll z = new NodeSll(i, null);
            z.next = x;
            return z;
        } else if (x.next != null && x.next.data > i) {
            NodeSll z = new NodeSll(i, null);
            z.next = x.next;
            x.next = z;
            return x;
        } else {
            x.next = insert(x.next, i);
            return x;
        }
    }

    public void main1(int n, int i) {
		// pre : x::sortll()
        NodeSll x = DataStructureFactory.createSortll(n);
		// inv : x::sortll()
        x = insert(x, i);
		// post : x::sortll()
        assert (new SortllTemplate()).check(x).res == 1;
    }
}
