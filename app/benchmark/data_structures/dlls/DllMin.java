package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;

public class DllMin {

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

    private boolean check(NodeDll prev, NodeDll x) {
        if (x == null)
            return true;
        else if (x.prev != prev)
            return false;
        else
            return check(x, x.next);
    }

    public void main1(NodeDll x) {
		// pre : x::dll()
        check(null, x);
		// inv : x::dll()
        NodeDll y = findMin(null, x);
		// post : x::dll()
        assert (new DllTemplate()).check(x).res == 1;
    }
}
