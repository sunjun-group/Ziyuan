package programs.sortlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SortllTemplate;
import templates.heap.StarTemplate;

public class SortllClone {

    private NodeSll clone(NodeSll x) {
        if (x == null)
            return null;
        else {
            NodeSll y = new NodeSll(x.data, null);
            y.next = clone(x.next);
            return y;
        }
    }

    public void main1(int n) {
		// pre : true
        NodeSll x = DataStructureFactory.createSortll(n);
		// inv : x::sortll()
        NodeSll y = clone(x);
		// post : x::sortll() * y::sortll()
        assert (new StarTemplate()).check((new SortllTemplate()).check(x), (new SortllTemplate()).check(y)).res == 1;
    }
}
