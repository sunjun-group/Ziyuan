package programs.rbts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeRB;
import templates.heap.RBTTemplate;

public class RbtInsert {

    private NodeRB leftRotate(NodeRB root, NodeRB x) {
        NodeRB y = x.right;
        x.right = y.left;
        if (y.left != null)
            y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;
        y.left = x;
        x.parent = y;
        return root;
    }

    private NodeRB rightRotate(NodeRB root, NodeRB x) {
        NodeRB y = x.left;
        x.left = y.right;
        if (y.right != null)
            y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null)
            root = y;
        else if (x == x.parent.right)
            x.parent.right = y;
        else
            x.parent.left = y;
        y.right = x;
        x.parent = y;
        return root;
    }

    private NodeRB insertBST(NodeRB root, NodeRB z) {
        if (root == null)
            return z;
        else if (z.data < root.data) {
            root.left = insertBST(root.left, z);
            root.left.parent = root;
        } else if (z.data > root.data) {
            root.right = insertBST(root.right, z);
            root.right.parent = root;
        }
        return root;
    }

    private NodeRB fixInsert(NodeRB root, NodeRB z) {
        if (z == root) {
            root.isBlack = true;
            return root;
        } else if (z.parent != null && z.parent.isBlack == false) {
            if (z.parent == z.parent.parent.left) {
                NodeRB y = z.parent.parent.right;
                if (y.isBlack == false) {
                    z.parent.isBlack = true;
                    y.isBlack = true;
                    z.parent.parent.isBlack = false;
                    root = fixInsert(root, z.parent.parent);
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        root = leftRotate(root, z);
                    }
                    z.parent.isBlack = true;
                    z.parent.parent.isBlack = false;
                    root = rightRotate(root, z.parent.parent);
                }
            } else {
                NodeRB y = z.parent.parent.left;
                if (y.isBlack == false) {
                    z.parent.isBlack = true;
                    y.isBlack = true;
                    z.parent.parent.isBlack = false;
                    root = fixInsert(root, z.parent.parent);
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        root = rightRotate(root, z);
                    }
                    z.parent.isBlack = true;
                    z.parent.parent.isBlack = false;
                    root = leftRotate(root, z.parent.parent);
                }
            }
            return root;
        } else
            return root;
    }

    private NodeRB insert(NodeRB root, int data) {
        NodeRB z = new NodeRB(data, false, null, null, null);
        root = insertBST(root, z);
        root = fixInsert(root, z);
        return root;
    }

    private boolean check(NodeRB root) {
        if (root == null)
            return true;
        else
            return check(root.left) && check(root.right);
    }

    public void main1(NodeRB root, int i) {
		// pre : root::rbt()
        check(root);
		// inv : root::rbt()
        root = insert(root, i);
		// post : root:rbt()
        assert (new RBTTemplate()).check(root).res == 1;
    }
}
