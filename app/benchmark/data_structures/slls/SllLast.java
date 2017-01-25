package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.LshdTemplate;

public class SllLast {

    private boolean check(NodeSll x) {
        if (x == null)
            return true;
        else
            return check(x.next);
    }

    private NodeSll last(NodeSll x) {
        if (x.next == null)
            return x;
        else
            return last(x.next);
    }

    public void main1(NodeSll x) {
		// pre : x::sll()
        check(x);
		// inv : x::sll()
        NodeSll res = last(x);
		// post : x::lseg(res) * res::node(_,null)
        assert (new LshdTemplate()).check(x, res).res == 1;
    }
}
