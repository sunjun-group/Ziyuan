package programs.sortlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;

public class SortllClean {

    private void free(NodeSll x) {
    }

    private void clean(NodeSll x) throws Exception {
        if (x != null) {
            NodeSll tmp = x;
            x = x.next;
            if (x != null && tmp.data > x.data)
                throw new Exception();
            free(tmp);
            clean(x);
        }
    }

    public void main1(int n) throws Exception {
		// pre : x::sortll()
        NodeSll x = DataStructureFactory.createSortll(n);
		// inv : x::sortll()
        clean(x);
		// post : true
    }
}
