package programs.sortlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SortllTemplate;

public class SortllDelete {

    private NodeSll delete(NodeSll x, int y) throws Exception {
        if (x == null)
            return null;
        if (x.next != null && x.data > x.next.data)
            throw new Exception();
        if (x.data == y)
            return x.next;
        else {
            x.next = delete(x.next, y);
            return x;
        }
    }

    public void main1(int n, int y) throws Exception {
		// pre : x::sortll()
        NodeSll x = DataStructureFactory.createSortll(n);
		// inv : x::sortll()
        x = delete(x, y);
		// post : x::sortll()
        assert (new SortllTemplate()).check(x).res == 1;
    }
}
