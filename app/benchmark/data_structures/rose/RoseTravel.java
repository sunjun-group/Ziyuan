package programs.rose;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeRose;
import programs.nodes.TreeRose;

public class RoseTravel {

    private boolean checkChild(NodeRose l, TreeRose p) {
        if (l == null)
            return true;
        else if (l.parent == p)
            return checkChild(l.next, p) && checkTree(l.child);
        else
            return false;
    }

    private boolean checkTree(TreeRose root) {
        if (root.children == null)
            return true;
        else
            return checkChild(root.children, root);
    }

    private boolean isNull(TreeRose root) {
        if (root == null)
            return true;
        else
            return false;
    }

    public void main1(TreeRose root) {
		// pre : root::mcf()
        isNull(root);
		// inv : root::mcf()
        boolean res = checkTree(root);
        assert res;
    }
}
