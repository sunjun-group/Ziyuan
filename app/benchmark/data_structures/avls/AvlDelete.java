package programs.avls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeAVL;
import templates.heap.AVLTemplate;

public class AvlDelete {

    private NodeAVL findMin(NodeAVL root) {
        if (root == null)
            return null;
        else if (root.left == null)
            return root;
        else
            return findMin(root.left);
    }

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

    private int getBalance(NodeAVL N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    private NodeAVL delete(NodeAVL root, int i) {
        if (root == null)
            return root;
        if (i < root.data)
            root.left = delete(root.left, i);
        else if (i > root.data)
            root.right = delete(root.right, i);
        else {
            if ((root.left == null) || (root.right == null)) {
                if (root.left != null)
                    root = root.right;
                else
                    root = root.left;
            } else {
                NodeAVL temp = findMin(root.right);
                root.data = temp.data;
                root.right = delete(root.right, temp.data);
            }
        }
        if (root == null)
            return root;
        int balance = getBalance(root);
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);
        if (balance < -1 && getBalance(root.right) > 0) {
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
        x = delete(x, i);
		// post : x::avl()
        assert (new AVLTemplate()).check(x).res == 1;
    }
}
