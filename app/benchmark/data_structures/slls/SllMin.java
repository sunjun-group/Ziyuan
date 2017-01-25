package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllMin {

    private NodeSll findMin(NodeSll min, NodeSll x) {
        if (x == null)
            return min;
        else {
            if (min == null)
                min = x;
            else if (min.data > x.data)
                min = x;
            return findMin(min, x.next);
        }
    }

    private boolean check(NodeSll x) {
        if (x == null)
            return true;
        else
            return check(x.next);
    }

    public void main1(NodeSll x) {
		// pre : x::sll()
        check(x);
		// inv : x::sll()
        NodeSll y = findMin(null, x);
		// post : x::sll()
        assert (new SllTemplate()).check(x).res == 1;
    }
}
