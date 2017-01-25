package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;

public class BstPreOrder {

    private boolean preOrder(NodeBST root) {
        if (root == null)
            return true;
        else {
            if (root.left != null && root.left.data > root.data)
                return false;
            if (root.right != null && root.right.data < root.data)
                return false;
            int x = root.data;
            boolean b1 = preOrder(root.left);
            boolean b2 = preOrder(root.right);
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
        boolean res = preOrder(x);
		// post : res
        assert res;
    }
}
