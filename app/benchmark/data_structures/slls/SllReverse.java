package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllReverse {

    private NodeSll reverse(NodeSll prev, NodeSll x) {
        if (x == null)
            return null;
        else if (x.next != null) {
            NodeSll tmp = x;
            x = tmp.next;
            tmp.next = prev;
            return reverse(tmp, x);
        } else {
            x.next = prev;
            return x;
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
        x = reverse(null, x);
		// post : x::sll()
        assert (new SllTemplate()).check(x).res == 1;
    }
}
