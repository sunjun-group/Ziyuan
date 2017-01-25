package programs.dlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeDll;
import templates.heap.DllTemplate;
import templates.*;
import templates.heap.*;
import templates.pure.*;
import templates.bag.*;

public class DllAppend {

	private void append(NodeDll x, NodeDll y) throws Exception {
		if (x.next != null)
			append(x.next, y);
		else {
	        x.next = y;
	        y.prev = x;
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

    public void main1(NodeDll x, NodeDll y) throws Exception {
		// pre : x::dll() * y::dll()
        check(null, x);
		// inv : x::dll() * y::dll()
        check(null, y);
		// inv : x::dll() * y::dll()
        x = append(x, y);
		// post : x::dll()
        assert (new DllTemplate()).check(x).res == 1;
    }
}
