package programs.clls;

import programs.nodes.DataStructureFactory;
import programs.nodes.NodeSll;

public class CllClean {

    private void free(NodeSll x) {
    }

    private void cleanRest(NodeSll root, NodeSll curr) {
        if (root == curr) {
            free(root);
        } else {
            NodeSll tmp = curr;
            curr = curr.next;
            free(tmp);
            cleanRest(root, curr);
        }
    }

    private void clean(NodeSll root) throws Exception {
        if (root == null)
            throw new Exception();
        else
            cleanRest(root, root.next);
    }

    private boolean checkRest(NodeSll root, NodeSll curr) {
        if (curr == null)
            return false;
        else if (root == curr)
            return true;
        else
            return checkRest(root, curr.next);
    }

    private boolean check(NodeSll root) {
        if (root == null)
            return false;
        else
            return checkRest(root, root.next);
    }

    public void main1(NodeSll x) throws Exception {
		// pre : x::cll()
        check(x);
		// inv : x::cll()
        clean(x);
		// post : true
    }
}
