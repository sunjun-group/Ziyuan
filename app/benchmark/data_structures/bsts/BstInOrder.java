package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;

public class BstInOrder {

    private boolean inOrder(NodeBST root) {
        if (root == null)
            return true;
        else {
            if (root.left != null && root.left.data > root.data)
                return false;
            if (root.right != null && root.right.data < root.data)
                return false;
            boolean b1 = inOrder(root.left);
            int x = root.data;
            boolean b2 = inOrder(root.right);
            return b1 && b2;
        }
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
        boolean res = inOrder(x);
		// post : res
        assert res;
    }
}
