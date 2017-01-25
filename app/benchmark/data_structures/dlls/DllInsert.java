package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;

public class DllInsert {

    private NodeDll insert(NodeDll x, int i) {
        if (x == null)
            return new NodeDll(i, null, null);
        else if (x.next == null) {
            NodeDll z = new NodeDll(i, null, null);
            x.next = z;
            z.prev = x;
            return x;
        } else {
            insert(x.next, i);
            return x;
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

    public void main1(NodeDll x, int i) {
		// pre : x::dll()
        check(null, x);
		// inv : x::dll()
        x = insert(x, i);
		// post : x::dll()
        assert (new DllTemplate()).check(x).res == 1;
    }
}
