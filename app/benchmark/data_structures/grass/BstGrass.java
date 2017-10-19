package programs.grass;

import programs.nodes.NodeBST2;
import templates.heap.BST2Template;

public class BstGrass {

    public void destroy(NodeBST2 root) {
        if (root != null) {
            destroy(root.left);
            destroy(root.right);
        }
    }

    public NodeBST2 insert(NodeBST2 root, int value) {
        if (root == null) {
            NodeBST2 t = new NodeBST2(value, null, null, null);
            return t;
        } else {
            NodeBST2 n = null;
            if (root.data > value) {
                n = insert(root.left, value);
                root.left = n;
                if (n != null)
                    n.parent = root;
                return root;
            } else if (root.data < value) {
                n = insert(root.right, value);
                root.right = n;
                if (n != null)
                    n.parent = root;
                return root;
            }
            return root;
        }
    }

    public void traverse(NodeBST2 root) {
        if (root != null) {
            traverse(root.left);
            traverse(root.right);
        }
    }

    public void main1(NodeBST2 root) {
        destroy(root);
        traverse(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main2(NodeBST2 root) {
        traverse(root);
        destroy(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main3(NodeBST2 root, int i) {
        root = insert(root, i);
        destroy(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main4(NodeBST2 root, int i) {
        root = insert(root, i);
        traverse(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main5(NodeBST2 root, int i) {
        traverse(root);
        root = insert(root, i);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main6(NodeBST2 root, int i) {
        destroy(root);
        root = insert(root, i);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main7(NodeBST2 root, int i) {
        traverse(root);
        destroy(root);
        root = insert(root, i);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main8(NodeBST2 root, int i) {
        destroy(root);
        traverse(root);
        root = insert(root, i);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main9(NodeBST2 root, int i) {
        traverse(root);
        root = insert(root, i);
        destroy(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main10(NodeBST2 root, int i) {
        destroy(root);
        root = insert(root, i);
        traverse(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main11(NodeBST2 root, int i) {
        root = insert(root, i);
        traverse(root);
        destroy(root);
        assert (new BST2Template()).check(root).res == 1;
    }

    public void main12(NodeBST2 root, int i) {
        root = insert(root, i);
        destroy(root);
        traverse(root);
        assert (new BST2Template()).check(root).res == 1;
    }
}
