package programs.slls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;

public class SllClean {

    private void free(NodeSll x) {
    }

    private void clean(NodeSll x) {
        if (x != null) {
            NodeSll tmp = x;
            x = x.next;
            free(tmp);
            clean(x);
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
        clean(x);
		// post : true
    }
}
