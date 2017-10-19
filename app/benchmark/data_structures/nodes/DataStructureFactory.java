package programs.nodes;

import templates.Utility;
import templates.heap.MCFTemplate;
import templates.heap.RoseTemplate;

public class DataStructureFactory {
	
	public static NodeSll createSll(int n) {
        if (n <= 0)
            return null;
        else {
            NodeSll x = new NodeSll(n, null);
            x.next = createSll(n - 1);
            return x;
        }
    }
	
	public static NodeSll createSortll(int data, int n) {
		if (data >= n) 
			return null;
		else {
			NodeSll y = createSortll(data + 1, n);
			// 
			NodeSll x = new NodeSll(data, null);
			// 
			x.next = y;
			return x;
		}
	}
	
	public static NodeSll createSortll(int n) {
		return createSortll(0, n);
	}
	
	private static NodeSll createCll(NodeSll root, int n) {
		if (n <= 0) return root;
		else if (root == null) {
			root = new NodeSll(n, null);
			root.next = createCll(root, n - 1);
			return root;
		} else {
			NodeSll x = new NodeSll(n, null);
			x.next = createCll(root, n - 1);
			return x;
		}
	}
	
	public static NodeSll createCll(int n) {
		return createCll(null, n);
	}
	
	public static NodeDll createDll(int n) {
		if (n <= 0) return null;
		else {
			NodeDll x = new NodeDll(n, null, null);
			NodeDll z = createDll(n - 1);
			x.next = z; 
			if (z != null) z.prev = x;
			return x;
		}
	}
	
	private static NodeBST createBst(int lower, int n) {
		if (n <= 0) return null;
		else if (n == 1) {
			return new NodeBST(lower + 1, null, null);
		} else {
			NodeBST root = new NodeBST(lower + (int) Math.pow(2, n - 1), null, null);
			root.left = createBst(lower, n - 1);
			root.right = createBst(lower + (int) Math.pow(2, n - 1), n - 1);
			return root;
		}
	}
	
	public static NodeBST createBst(int n) {
		return createBst(0, n);
	}
	
	private static NodeAVL createAvl(int lower, int n) {
		if (n <= 0) return null;
		else if (n == 1) {
			return new NodeAVL(lower + 1, null, null);
		} else {
			NodeAVL root = new NodeAVL(lower + (int) Math.pow(2, n - 1), null, null);
			root.left = createAvl(lower, n - 1);
			root.right = createAvl(lower + (int) Math.pow(2, n - 1), n - 1);
			return root;
		}
	}
	
	public static NodeAVL createAvl(int n) {
		return createAvl(0, n);
	}
	
	private static NodeRB createRbt(int lower, int n) {
		if (n <= 0) return null;
		else if (n == 1) {
			return new NodeRB(lower + 1, true, null, null, null);
		} else {
			NodeRB root = new NodeRB(lower + (int) Math.pow(2, n - 1), true, null, null, null);
			NodeRB left = createRbt(lower, n - 1);
			NodeRB right = createRbt(lower + (int) Math.pow(2, n - 1), n - 1);
			root.left = left; left.parent = root;
			root.right = right; right.parent = root;
			return root;
		}
	}
	
	public static NodeRB createRbt(int n) {
		return createRbt(0, n);
	}
	
	private static NodeRose createRoseN(int n, int l, TreeRose parent) {
		if (l <= 0) return null;
		else {
			NodeRose node = new NodeRose();
			node.next = createRoseN(n, l - 1, parent);
			node.parent = parent;
			node.child = createRoseT(n - 1);
			return node;
		}
	}
	
	private static TreeRose createRoseT(int n) {
		if (n <= 0) return new TreeRose();
		else {
			TreeRose tree = new TreeRose();
			NodeRose children = createRoseN(n, n, tree);
			tree.children = children;
			return tree;
		}
	}
	
	public static TreeRose createRose(int n) {
		return createRoseT(n);
	}
	
	private static NodeMCF createMCFN(int n, int l, NodeMCF prev, TreeMCF parent) {
		if (l <= 0) return null;
		else {
			NodeMCF node = new NodeMCF();
			node.next = createMCFN(n, l - 1, node, parent);
			node.prev = prev;
			node.parent = parent;
			node.child = createMCFT(n - 1);
			return node;
		}
	}
	
	private static TreeMCF createMCFT(int n) {
		if (n <= 0) return new TreeMCF();
		else {
			TreeMCF tree = new TreeMCF();
			NodeMCF children = createMCFN(n, n, null, tree);
			tree.children = children;
			return tree;
		}
	}
	
	public static TreeMCF createMCF(int n) {
		return createMCFT(n);
	}
	
	public static NodeTll createTree(int n) {
		if (n <= 0) return null;
		else if (n == 1) {
			return new NodeTll();
		} else {
			NodeTll root = new NodeTll();
			NodeTll left = createTree(n - 1);
			NodeTll right = createTree(n - 1);
			root.left = left; root.right = right;
//			left.parent = root; right.parent = root;
			return root;
		}
	}
	
	public static void main(String[] args) {
//		TreeRose tree = createRose(0);
//		System.out.println((new RoseTemplate()).check(tree));
//		
//		tree = createRose(1);
//		System.out.println((new RoseTemplate()).check(tree));
//	
//		tree = createRose(2);
//		System.out.println((new RoseTemplate()).check(tree));
		
		TreeMCF mcf = createMCF(0);
		System.out.println((new MCFTemplate()).check(null));
		
		mcf = createMCF(1);
		System.out.println((new MCFTemplate()).check(mcf));
		
		mcf = createMCF(2);
		System.out.println((new MCFTemplate()).check(mcf));
		
//		Utility.toNew(mcf);
		
		Utility.fieldToNew(mcf);
		System.out.println((new MCFTemplate()).check(mcf));
	}

}
