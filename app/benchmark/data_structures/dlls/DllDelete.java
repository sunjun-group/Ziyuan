package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;

public class DllDelete {

    private NodeDll delete1(NodeDll x, int y) throws Exception {
        if (x == null)
            return null;
        if (x.next != null && x.next.prev != x)
            throw new Exception();
        if (x.data == y) {
            if (x.next != null)
                x.next.prev = x.prev;
            return x.next;
        } else {
            x.next = delete1(x.next, y);
            return x;
        }
    }

    private NodeDll delete(NodeDll x, int y) throws Exception {
        if (x == null)
            return null;
        else if (x.prev != null)
            throw new Exception();
        return delete1(x, y);
    }

    private boolean check(NodeDll prev, NodeDll x) {
        if (x == null)
            return true;
        else if (x.prev != prev)
            return false;
        else
            return check(x, x.next);
    }

    public void main1(NodeDll x, int y) throws Exception {
		// pre : x::dll()
        check(null, x);
		// inv : x::dll()
        x = delete(x, y);
		// post : x::dll()
        assert (new DllTemplate()).check(x).res == 1;
    }
}
