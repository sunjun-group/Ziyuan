package programs.rose;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeMCF;
import programs.nodes.TreeMCF;

public class McfTravel {

    private boolean checkChild(NodeMCF l, NodeMCF prev, TreeMCF parent) {
        if (l == null)
            return true;
        else if (l.parent == parent && l.prev == prev)
            return checkChild(l.next, l, parent) && checkTree(l.child);
        else
            return false;
    }

    private boolean checkTree(TreeMCF root) {
        if (root.children == null)
            return true;
        else {
        	System.out.println("abcdef");
            return checkChild(root.children, null, root);
        }
    }

    private boolean isNull(TreeMCF root) {
        if (root == null)
            return true;
        else
            return false;
    }

    public void main1(TreeMCF root) {
		// pre : root::mcf()
        isNull(root);
		// inv : root:: mcf()
        boolean res = checkTree(root);
        assert res;
    }
}
