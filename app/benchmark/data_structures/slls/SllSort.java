package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllSort {

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

    private NodeSll delete(NodeSll x, int i) {
        if (x == null)
            return null;
        else if (x.data == i)
            return x.next;
        else {
            x.next = delete(x.next, i);
            return x;
        }
    }

    private NodeSll sort(NodeSll x) {
        if (x == null)
            return null;
        else {
            NodeSll min = findMin(null, x);
            NodeSll y = new NodeSll(min.data, null);
            x = delete(x, min.data);
            y.next = sort(x);
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
        NodeSll y = sort(x);
		// post : x::sll()
        assert (new SllTemplate()).check(y).res == 1;
    }
}
