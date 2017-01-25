package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;

public class ToDll {

    private boolean check(NodeDll prev, NodeDll x) {
        if (x == null)
            return true;
        else if (x.prev != prev)
            return false;
        else
            return check(x, x.next);
    }

    private void toDll(NodeDll prev, NodeDll curr) {
        if (curr != null) {
            curr.prev = prev;
            toDll(curr, curr.next);
        }
    }

    public void main1(NodeDll x) throws Exception {
		// pre :: x:sll2()
        check(null, x);
		// inv : x::sll2()
        toDll(null, x);
        assert (new DllTemplate()).check(x).res == 1;
    }
}
