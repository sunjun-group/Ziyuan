package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;

public class DllClean {

    private void free(NodeDll x) {
    }

    private void clean1(NodeDll x) throws Exception {
        if (x != null) {
            NodeDll tmp = x;
            if (x.next != null && x.next.prev != x)
                throw new Exception();
            x = x.next;
            free(tmp);
            clean1(x);
        }
    }

    private void clean(NodeDll x) throws Exception {
        if (x == null)
            return;
        else if (x.prev != null)
            throw new Exception();
        else
            clean1(x);
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
        clean(x);
		// post : true
    }
}
