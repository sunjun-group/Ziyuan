package programs.simple;

import programs.nodes.NodeSll;
import templates.heap.SllTemplate;
import templates.heap.StarTemplate;

public class MoEx {

    public NodeSll createSll(int n) {
        if (n <= 0)
            return null;
        else {
            NodeSll x = new NodeSll(n, null);
            x.next = createSll(n - 1);
            return x;
        }
    }

    public int getSum(NodeSll x, NodeSll y) {
        int sum = 0;
        if (x != null) {
            sum += x.data + y.data;
            sum += getSum(x.next, y.next);
            return sum;
        }
        return sum;
    }

    public void main1(int m, int n) {
		// pre : n >= m
        NodeSll x = createSll(m);
		// inv : x = null || x::sll() && n >= len(x)
        NodeSll y = createSll(n);
		// inv : x = null && y::sll() || x::sll() * y::sll() && len(y) >= len(x)
        getSum(x, y);
        assert (new StarTemplate()).check((new SllTemplate()).check(x), (new SllTemplate()).check(y)).res == 1;
    }
}
