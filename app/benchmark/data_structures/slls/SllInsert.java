package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllInsert {

    private NodeSll insert(NodeSll x, int i) {
        if (x == null)
            return new NodeSll(i, null);
        else {
            x.next = insert(x.next, i);
            return x;
        }
    }

    private boolean check(NodeSll x) {
        if (x == null)
            return true;
        else
            return check(x.next);
    }

    public void main1(NodeSll x, int i) {
		// pre : x::sll()
        check(x);
		// inv : x::sll()
        x = insert(x, i);
		// post : x::sll()
        assert (new SllTemplate()).check(x).res == 1;
    }
}
