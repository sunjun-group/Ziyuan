package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;

public class DllSort {

    private NodeDll findMin(NodeDll min, NodeDll x) {
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

    private NodeDll delete(NodeDll x, NodeDll y) throws Exception {
        if (x == null)
            return null;
        if (x.next != null && x.next.prev != x)
            throw new Exception();
        if (x == y) {
            if (x.next != null)
                x.next.prev = x.prev;
            return x.next;
        } else {
            x.next = delete(x.next, y);
            return x;
        }
    }

    private NodeDll sort1(NodeDll x) throws Exception {
        if (x == null)
            return null;
        else {
            NodeDll min = findMin(null, x);
            NodeDll y = new NodeDll(min.data, null, null);
            x = delete(x, min);
            NodeDll z = sort1(x);
            y.next = z;
            if (z != null)
                z.prev = y;
            return y;
        }
    }

    private NodeDll sort(NodeDll x) throws Exception {
        if (x == null)
            return null;
        if (x.prev != null)
            throw new Exception();
        else
            return sort1(x);
    }

    private boolean check(NodeDll prev, NodeDll x) {
        if (x == null)
            return true;
        else if (x.prev != prev)
            return false;
        else
            return check(x, x.next);
    }

    public void main1(NodeDll x) throws Exception {
		// pre : x::dll()
        check(null, x);
		// inv : x::dll()
        NodeDll y = sort(x);
		// post : x::dll()
        assert (new DllTemplate()).check(y).res == 1;
    }
}
