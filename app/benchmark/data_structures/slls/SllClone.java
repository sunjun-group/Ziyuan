package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;
import templates.heap.StarTemplate;

public class SllClone {

    private NodeSll clone(NodeSll x) {
        if (x == null)
            return null;
        else {
            NodeSll y = new NodeSll(x.data, null);
            y.next = clone(x.next);
            return y;
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
        NodeSll y = clone(x);
		// post : x::sll() * y::sll()
        assert (new StarTemplate()).check((new SllTemplate()).check(x), (new SllTemplate()).check(y)).res == 1;
    }
}
