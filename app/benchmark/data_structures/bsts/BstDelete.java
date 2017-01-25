package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;
import templates.heap.BSTTemplate;

public class BstDelete {

    private NodeBST findMin(NodeBST root) {
        if (root == null)
            return null;
        else if (root.left == null)
            return root;
        else
            return findMin(root.left);
    }

    private NodeBST delete(NodeBST root, int i) {
        if (root == null)
            return null;
        else if (i < root.data) {
            root.left = delete(root.left, i);
            return root;
        } else if (i > root.data) {
            root.right = delete(root.right, i);
            return root;
        } else {
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;
            else {
                NodeBST z = findMin(root.right);
                root.data = z.data;
                root.right = delete(root.right, z.data);
                return root;
            }
        }
    }

    private boolean check(NodeBST root) {
        if (root == null)
            return true;
        else
            return check(root.left) && check(root.right);
    }

    public void main1(NodeBST x, int i) {
		// pre : x::bst()
        check(x);
		// inv : x::bst()
        x = delete(x, i);
		// post : x::bst()
        assert (new BSTTemplate()).check(x).res == 1;
    }
}
