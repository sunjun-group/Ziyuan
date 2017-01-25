package programs.bsts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeBST;
import templates.heap.BSTTemplate;

public class BstInsert {

    private NodeBST insert(NodeBST root, int i) {
        if (root == null)
            return new NodeBST(i, null, null);
        else {
            if (i < root.data)
                root.left = insert(root.left, i);
            else if (i > root.data)
                root.right = insert(root.right, i);
            return root;
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
        x = insert(x, i);
		// post : x::bst()
        assert (new BSTTemplate()).check(x).res == 1;
    }
}
