package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;
import templates.heap.BSTTemplate;

public class BstMin {

    private NodeBST findMin(NodeBST root) {
        if (root == null)
            return null;
        else if (root.left == null)
            return root;
        else
            return findMin(root.left);
    }

    private boolean check(NodeBST root) {
        if (root == null)
            return true;
        else
            return check(root.left) && check(root.right);
    }

    public void main1(NodeBST x) {
		// pre : x::bst()
        check(x);
		// inv : x::bst()
        findMin(x);
		// post : x::bst()
        assert (new BSTTemplate()).check(x).res == 1;
    }
}
