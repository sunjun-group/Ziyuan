package programs.avls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeAVL;
import templates.heap.AVLTemplate;

public class AvlInsert {

    private int height(NodeAVL root) {
        if (root == null)
            return 0;
        return Math.max(height(root.left), height(root.right)) + 1;
    }

    private NodeAVL rightRotate(NodeAVL root) {
        NodeAVL left = root.left;
        NodeAVL right = left.right;
        left.right = root;
        root.left = right;
        return left;
    }

    private NodeAVL leftRotate(NodeAVL root) {
        NodeAVL right = root.right;
        NodeAVL left = right.left;
        right.left = root;
        root.right = left;
        return right;
    }

    private int getBalance(NodeAVL x) {
        if (x == null)
            return 0;
        return height(x.left) - height(x.right);
    }

    private NodeAVL insert(NodeAVL root, int i) {
        if (root == null)
            return (new NodeAVL(i, null, null));
        if (i < root.data)
            root.left = insert(root.left, i);
        else if (i > root.data)
            root.right = insert(root.right, i);
        else
            return root;
        int balance = getBalance(root);
        if (balance > 1 && i < root.left.data)
            return rightRotate(root);
        if (balance < -1 && i > root.right.data)
            return leftRotate(root);
        if (balance > 1 && i > root.left.data) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        if (balance < -1 && i < root.right.data) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }
        return root;
    }

    private boolean check(NodeAVL root) {
        if (root == null)
            return true;
        else
            return check(root.left) && check(root.right);
    }

    public void main1(NodeAVL x, int i) {
		// pre : x::avl()
        check(x);
		// inv : x::avl()
        x = insert(x, i);
		// post : x::avl()
        assert (new AVLTemplate()).check(x).res == 1;
    }
}
