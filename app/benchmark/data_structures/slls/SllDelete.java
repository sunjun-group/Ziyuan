package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllDelete {

    private NodeSll delete(NodeSll x, int y) {
        if (x == null)
            return null;
        else if (x.data == y)
            return x.next;
        else {
            x.next = delete(x.next, y);
            return x;
        }
    }

    private boolean check(NodeSll x) {
        if (x == null)
            return true;
        else
            return check(x.next);
    }

    public void main1(NodeSll x, int y) {
		// pre : x::sll()
        check(x);
		// inv : x::sll()
        x = delete(x, y);
		// post : x::sll()
        assert (new SllTemplate()).check(x).res == 1;
    }
}
