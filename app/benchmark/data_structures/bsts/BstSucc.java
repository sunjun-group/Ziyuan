package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;
import templates.heap.BSTTemplate;

public class BstSucc {

    private NodeBST findMin(NodeBST root) {
        if (root == null)
            return null;
        else if (root.left == null)
            return root;
        else
            return findMin(root.left);
    }

    private NodeBST findSucc(NodeBST root) {
        if (root == null)
            return null;
        else
            return findMin(root.right);
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
        findSucc(x);
		// post : x::bst()
        assert (new BSTTemplate()).check(x).res == 1;
    }
}
