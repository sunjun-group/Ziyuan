package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllAppend {

    private NodeSll append(NodeSll x, NodeSll y) {
        if (x == null)
            return y;
        else {
            x.next = append(x.next, y);
            return x;
        }
    }

    private boolean check(NodeSll x) {
        if (x == null)
            return true;
        else
            return check(x.next);
    }

    public void main1(NodeSll x, NodeSll y) {
		// pre : x::sll() * y::sll()
        check(x);
		// inv : x::sll() * y::sll()
        check(y);
		// inv : x::sll() * y::sll()
        x = append(x, y);
		// post : x::sll()
        assert (new SllTemplate()).check(x).res == 1;
    }
}
