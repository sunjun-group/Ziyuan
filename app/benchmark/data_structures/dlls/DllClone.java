package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;
import templates.heap.StarTemplate;

public class DllClone {

    private NodeDll clone(NodeDll x) {
        if (x == null)
            return null;
        else {
            NodeDll y = new NodeDll(x.data, null, null);
            NodeDll z = clone(x.next);
            y.next = z;
            if (z != null)
                z.prev = y;
            return y;
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
        NodeDll y = clone(x);
		// post : x::dll * y::dll()
        assert (new StarTemplate()).check((new DllTemplate()).check(x), (new DllTemplate()).check(y)).res == 1;
    }
}
