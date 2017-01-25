package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;

public class DllReverse {

    private NodeDll reverse(NodeDll x) throws Exception {
        if (x == null)
            return null;
        else if (x.next != null && x.next.prev != x) {
            throw new Exception();
        } else if (x.next != null && x.next.prev == x) {
            NodeDll tmp = x;
            x = tmp.next;
            tmp.next = tmp.prev;
            tmp.prev = x;
            return reverse(x);
        } else {
            x.next = x.prev;
            x.prev = null;
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

    public void main1(NodeDll x) throws Exception {
		// pre : x::dll()
        check(null, x);
		// inv : x::dll()
        x = reverse(x);
		// post : x::dll()
        assert (new DllTemplate()).check(x).res == 1;
    }
}
