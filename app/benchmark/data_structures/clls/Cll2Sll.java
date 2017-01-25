package programs.clls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class Cll2Sll {

    private NodeSll toSllRest(NodeSll root, NodeSll curr) {
        if (root == curr)
            return null;
        else {
            curr.next = toSllRest(root, curr.next);
            return curr;
        }
    }

    private NodeSll toSll(NodeSll root) throws Exception {
        if (root == null)
            throw new Exception();
        else {
            root.next = toSllRest(root, root.next);
            return root;
        }
    }

    private boolean checkRest(NodeSll root, NodeSll curr) {
        if (curr == null)
            return false;
        else if (root == curr)
            return true;
        else
            return checkRest(root, curr.next);
    }

    private boolean check(NodeSll root) {
        if (root == null)
            return false;
        else
            return checkRest(root, root.next);
    }

    public void main1(NodeSll x) throws Exception {
		// pre : x::cll()
        check(x);
		// inv : x::cll()
        NodeSll y = toSll(x);
		// post : y::sll()
        assert (new SllTemplate()).check(y).res == 1;
    }
}
