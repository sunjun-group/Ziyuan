package programs.clls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;
import templates.heap.CllTemplate;

public class CllMin {

    private NodeSll findMinRest(NodeSll root, NodeSll curr, NodeSll min) {
        if (root == curr)
            return min;
        else {
            if (min.data > curr.data)
                min = curr;
            return findMinRest(root, curr.next, min);
        }
    }

    private NodeSll findMin(NodeSll root) throws Exception {
        if (root == null)
            throw new Exception();
        else
            return findMinRest(root, root.next, root);
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
        NodeSll y = findMin(x);
		// post : x::cll()
        assert (new CllTemplate()).check(x).res == 1;
    }
}
