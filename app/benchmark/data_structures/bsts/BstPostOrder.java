package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;

public class BstPostOrder {

    private boolean postOrder(NodeBST root) {
        if (root == null)
            return true;
        else {
            if (root.left != null && root.left.data > root.data)
                return false;
            if (root.right != null && root.right.data < root.data)
                return false;
            boolean b1 = postOrder(root.left);
            boolean b2 = postOrder(root.right);
            int x = root.data;
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
        boolean res = postOrder(x);
		// post : res
        assert res;
    }
}
