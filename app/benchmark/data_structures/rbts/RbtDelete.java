package programs.rbts;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeRB;
import templates.heap.RBTTemplate;

public class RbtDelete {

    private NodeRB transplant(NodeRB root, NodeRB u, NodeRB v) {
        if (u.parent == null)
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;
        if (v != null)
            v.parent = u.parent;
        return root;
    }

    private NodeRB findMin(NodeRB root) {
        if (root == null || root.left == null)
            return root;
        else
            return findMin(root.left);
    }

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

    private NodeRB fixDelete(NodeRB root, NodeRB x, NodeRB y) {
        if (x == root || (x != null && x.isBlack == false)) {
            if (x != null)
                x.isBlack = true;
            return root;
        } else {
            if (y != null && x == y.left) {
                NodeRB w = y.right;
                if (w.isBlack == false) {
                    w.isBlack = true;
                    y.isBlack = false;
                    root = leftRotate(root, y);
                    w = y.right;
                }
                if ((w.left == null || w.left.isBlack == true) && (w.right == null || w.right.isBlack == true)) {
                    w.isBlack = false;
                    root = fixDelete(root, y, y.parent);
                } else {
                    if (w.right == null || w.right.isBlack == true) {
                        if (w.left != null)
                            w.left.isBlack = true;
                        w.isBlack = false;
                        root = rightRotate(root, w);
                        w = y.right;
                    }
                    w.isBlack = y.isBlack;
                    y.isBlack = true;
                    if (w.right != null)
                        w.right.isBlack = true;
                    root = leftRotate(root, y);
                    root = fixDelete(root, root, root.parent);
                }
            } else if (y != null && x == y.right) {
                NodeRB w = y.left;
                if (w.isBlack == false) {
                    w.isBlack = true;
                    y.isBlack = false;
                    root = rightRotate(root, y);
                    w = y.left;
                }
                if ((w.left == null || w.left.isBlack == true) && (w.right == null || w.right.isBlack == true)) {
                    w.isBlack = false;
                    root = fixDelete(root, y, y.parent);
                } else {
                    if (w.left == null || w.left.isBlack == true) {
                        if (w.right != null)
                            w.right.isBlack = true;
                        w.isBlack = false;
                        root = leftRotate(root, w);
                        w = y.left;
                    }
                    w.isBlack = y.isBlack;
                    y.isBlack = true;
                    if (w.left != null)
                        w.left.isBlack = true;
                    root = rightRotate(root, y);
                    root = fixDelete(root, root, root.parent);
                }
            }
            return root;
        }
    }

    private NodeRB delete(NodeRB root, NodeRB z) {
        NodeRB y = z;
        NodeRB x = null, xParent = null;
        boolean origColor = y.isBlack;
        if (z.left == null) {
            x = z.right;
            xParent = z.parent;
            root = transplant(root, z, z.right);
        } else if (z.right == null) {
            x = z.left;
            xParent = z.parent;
            root = transplant(root, z, z.left);
        } else {
            y = findMin(z.right);
            origColor = y.isBlack;
            x = y.right;
            if (y.parent == z) {
                xParent = y;
            } else {
                root = transplant(root, y, y.right);
                y.right = z.right;
                if (y.right != null)
                    y.right.parent = y;
                xParent = y.parent;
            }
            root = transplant(root, z, y);
            y.left = z.left;
            if (y.left != null)
                y.left.parent = y;
            y.isBlack = z.isBlack;
        }
        if (origColor == true)
            root = fixDelete(root, x, xParent);
        return root;
    }

    private NodeRB delete(NodeRB root, NodeRB z, int key) {
        if (z == null)
            return root;
        else if (z.data == key)
            return delete(root, z);
        else if (z.data < key)
            return delete(root, z.right, key);
        else
            return delete(root, z.left, key);
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
		// nothing
        root = delete(root, root, i);
		// post : root::rbt()
        assert (new RBTTemplate()).check(root).res == 1;
    }
}
