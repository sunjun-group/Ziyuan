package programs.sortlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SortllTemplate;

public class SortllTravel {
	
    private boolean travel(NodeSll x) {
        if (x == null)
            return true;
        else if (x.next != null && x.next.data < x.data)
            return false;
        else
            return travel(x.next);
    }

    public void main1(NodeSll x) {
		// pre : x::sortll()
        NodeSll x = DataStructureFactory.createSortll(n);
		// inv : x::sortll()
        boolean res = travel(x);
		// post : x::sortll()
        assert res;
    }
}
