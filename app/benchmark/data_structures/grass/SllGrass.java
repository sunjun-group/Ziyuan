package programs.grass;

import programs.nodes.NodeSll;
import templates.heap.SllTemplate;

public class SllGrass {

    public NodeSll concat(NodeSll a, NodeSll b) {
        if (a == null) {
            return b;
        } else {
            NodeSll curr = a;
            if (curr.next == null) {
                curr.next = b;
                return a;
            } else {
                curr.next = concat(curr.next, b);
                return a;
            }
        }
    }

    public void dispose(NodeSll lst) {
        if (lst != null) {
            NodeSll n = lst.next;
            dispose(n);
        }
    }

    public NodeSll insert(NodeSll lst, NodeSll elt) {
        if (lst == null)
            return elt;
        else {
            if (lst.next == null) {
                elt.next = lst;
                return elt;
            } else {
                NodeSll n1 = lst.next;
                NodeSll n2 = insert(n1, elt);
                lst.next = n2;
                return lst;
            }
        }
    }

    public NodeSll remove(NodeSll lst) {
        if (lst == null)
            return null;
        else if (lst.next == null) {
            NodeSll n = lst.next;
            return n;
        } else {
            NodeSll n1 = lst.next;
            NodeSll n2 = remove(n1);
            lst.next = n2;
            return lst;
        }
    }

    public void traverse(NodeSll lst) {
        if (lst != null) {
            NodeSll n = lst.next;
            traverse(n);
        }
    }

    public void main1(NodeSll x) {
        traverse(x);
        dispose(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main2(NodeSll x) {
        dispose(x);
        traverse(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main3(NodeSll x) {
        traverse(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main4(NodeSll x) {
        NodeSll y = remove(x);
        traverse(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main5(NodeSll x) {
        dispose(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main6(NodeSll x) {
        NodeSll y = remove(x);
        dispose(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main7(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        dispose(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main8(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        traverse(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main9(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main10(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        dispose(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main11(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        traverse(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main12(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main13(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        dispose(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main14(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        NodeSll y = remove(x);
        dispose(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main15(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        traverse(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main16(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        NodeSll y = remove(x);
        traverse(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main17(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        dispose(x);
        traverse(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main18(NodeSll a, NodeSll b) {
        NodeSll x = concat(a, b);
        traverse(x);
        dispose(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main19(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        dispose(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main20(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        NodeSll y = remove(x);
        dispose(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main21(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        traverse(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main22(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        NodeSll y = remove(x);
        traverse(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main23(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        dispose(x);
        traverse(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main24(NodeSll a, NodeSll b) {
        NodeSll x = insert(a, b);
        traverse(x);
        dispose(x);
        assert (new SllTemplate()).check(x).res == 1;
    }

    public void main25(NodeSll x) {
        traverse(x);
        dispose(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main26(NodeSll x) {
        dispose(x);
        traverse(x);
        NodeSll y = remove(x);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main27(NodeSll x) {
        traverse(x);
        NodeSll y = remove(x);
        dispose(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main28(NodeSll x) {
        dispose(x);
        NodeSll y = remove(x);
        traverse(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main29(NodeSll x) {
        NodeSll y = remove(x);
        traverse(y);
        dispose(y);
        assert (new SllTemplate()).check(y).res == 1;
    }

    public void main30(NodeSll x) {
        NodeSll y = remove(x);
        dispose(y);
        traverse(y);
        assert (new SllTemplate()).check(y).res == 1;
    }
}
