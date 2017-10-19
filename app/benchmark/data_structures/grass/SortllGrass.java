package programs.grass;

import programs.nodes.NodeSll;
import templates.heap.SortllTemplate;

public class SortllGrass {

    public NodeSll concat(NodeSll a, NodeSll b) {
        if (a == null) {
            return b;
        } else {
            a.next = concat(a.next, b);
            return a;
        }
    }

    public void dispose(NodeSll lst) {
        NodeSll curr = lst;
        if (curr != null) {
            NodeSll tmp = curr;
            curr = curr.next;
            dispose(curr);
        }
    }

    public NodeSll insert(NodeSll lst, NodeSll elt) {
        if (lst == null || lst.data > elt.data) {
            elt.next = lst;
            return elt;
        } else {
            NodeSll curr = lst;
            if (curr.next != null && curr.next.data <= elt.data) {
                curr.next = insert(curr.next, elt);
                return curr;
            } else {
                elt.next = curr.next;
                curr.next = elt;
                return curr;
            }
        }
    }

    public void traverse(NodeSll lst) {
        NodeSll curr = lst;
        if (curr != null) {
            traverse(curr.next);
        }
    }

    public void main1(NodeSll x) {
        dispose(x);
        traverse(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main2(NodeSll x) {
        traverse(x);
        dispose(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main3(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        dispose(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main4(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        traverse(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main5(NodeSll a, NodeSll b) {
        dispose(a);
        NodeSll x = insert(a, b);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main6(NodeSll a, NodeSll b) {
        traverse(a);
        NodeSll x = insert(a, b);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main7(NodeSll a, NodeSll b) {
        dispose(a);
        traverse(a);
        NodeSll x = insert(a, b);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main8(NodeSll a, NodeSll b) {
        traverse(a);
        dispose(a);
        NodeSll x = insert(a, b);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main9(NodeSll a, NodeSll b) {
        dispose(a);
        NodeSll x = insert(a, b);
        traverse(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main10(NodeSll a, NodeSll b) {
        traverse(a);
        NodeSll x = insert(a, b);
        dispose(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main11(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        dispose(x);
        traverse(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }

    public void main12(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        traverse(x);
        dispose(x);
        assert (new SortllTemplate()).check(x).res == 1;
    }
}
