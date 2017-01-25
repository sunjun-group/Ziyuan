package programs.tlls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeTll;
import templates.heap.TllTemplate;

public class TllSetRight {

    private NodeTll setRight(NodeTll x, NodeTll t) {
        if (x.left == null && x.right == null) {
            x.next = t;
            return x;
        } else {
            NodeTll l_most = setRight(x.right, t);
            return setRight(x.left, l_most);
        }
    }

    private boolean check(NodeTll root) {
        if (root == null)
            return true;
        else
            return check(root.left) && check(root.right);
    }

    public void main1(NodeTll root) {
		// pre : root::tll()
        check(root);
		// inv : root::tll()
        setRight(root, null);
        assert (new TllTemplate()).check(root).res == 1;
    }
}
