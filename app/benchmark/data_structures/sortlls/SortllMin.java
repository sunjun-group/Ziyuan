package programs.sortlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SortllTemplate;

public class SortllMin {

    private NodeSll findMin(NodeSll x) {
        return x;
    }

    public void main1(NodeSll x) {
		// pre : x::sortll()
        NodeSll x = DataStructureFactory.createSortll(n);
		// inv : x::sortll()
        NodeSll y = findMin(x);
		// post : x::sortll()
        assert (new SortllTemplate()).check(x).res == 1;
    }
}
