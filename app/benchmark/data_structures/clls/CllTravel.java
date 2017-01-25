package programs.clls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;

public class CllTravel {

    private boolean travel(NodeSll root, NodeSll curr) {
        if (root == curr)
            return true;
        else
            return travel(root, curr.next);
    }

    private boolean isNull(NodeSll x) {
        if (x == null)
            return true;
        else
            return false;
    }

    public void main1(NodeSll x) throws Exception {
		// pre : x::cll()
        isNull(x);
		// inv : x::cll()
        boolean b = travel(x, x.next);
        assert b;
    }
}
