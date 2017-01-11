package assertion.template.checker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import invariant.templates.CompositeTemplate;
import invariant.templates.SingleTemplate;
import invariant.templates.Template;
import sav.strategies.dto.execute.value.ExecValue;

class Pair {
	public int pass;
	
	public int fail;
	
	public boolean isCut;
	
	public Pair(int p, int f) {
		pass = p; fail = f;
		isCut = false;
	}
	
	@Override
	public String toString() {
		return "pass = " + pass + "; fail = " + fail;
	}
}

class Graph {
	public List<Pair> edges;
	
	public int numEdges;
	
	public Graph(List<Pair> edges) {
		this.edges = edges;
		numEdges = edges.size();
	}
}

public class PACChecker {
	
	private List<List<ExecValue>> passValues;
	
	private List<List<ExecValue>> failValues;
	
	private List<SingleTemplate> primTemplates;
	
	private List<CompositeTemplate> validTemplates;
	
	private int[][] matrix;
	
	private Graph g;
	
	public PACChecker(
			List<List<ExecValue>> passValues,
			List<List<ExecValue>> failValues,
			List<SingleTemplate> templates) {
		this.passValues = passValues;
		this.failValues = failValues;
		this.primTemplates = templates;
		
		validTemplates = new ArrayList<CompositeTemplate>();
	}
	
	public List<CompositeTemplate> getValidTemplates() {
		return validTemplates;
	}
	
	public boolean notCut(List<Pair> edges, int i, int j) {
		Iterator<Pair> it = edges.iterator();
		while (it.hasNext()) {
			Pair p = it.next();
			if (p.pass == i && p.fail == j) {
				if (!p.isCut) {
					return true;
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	public void buildMatrix() {
		int numRow = passValues.size() + failValues.size();
		int numCol = primTemplates.size();
		
		int passSize = passValues.size();
		
		matrix = new int[numRow][numCol];
		
		for (int i = 0; i < numRow; i++) {
			for (int j = 0; j < numCol; j++) {
				if (i < passSize) {
					SingleTemplate st = (SingleTemplate) primTemplates.get(j);
					if (st.checkPassValue(st.getPassExecValuesList().get(i)))
						matrix[i][j] = 1;
					else if (st.checkFailValue(st.getPassExecValuesList().get(i)))
						matrix[i][j] = 0;
					else
						matrix[i][j] = -1;
				} else {
					SingleTemplate st = (SingleTemplate) primTemplates.get(j);
					if (st.checkPassValue(st.getFailExecValuesList().get(i - passSize)))
						matrix[i][j] = 1;
					else if (st.checkFailValue(st.getFailExecValuesList().get(i - passSize)))
						matrix[i][j] = 0;
					else
						matrix[i][j] = -1;
				}
			}
		}
	}
	
	public void buildGraph() {
		int passSize = passValues.size();
		
		List<Pair> edges = new ArrayList<Pair>();
		
		for (int i = 0; i < passValues.size(); i++) {
			for (int j = 0; j < failValues.size(); j++) {
				Pair p = new Pair(i, j + passSize);
				edges.add(p);
			}
		}
		
		g = new Graph(edges);
	}
	
	public List<Integer> chooseTemplatesIndex() {
		int numCol = primTemplates.size();
		
		int passSize = passValues.size();
		
		List<Pair> edges = g.edges;
		
		List<Integer> chosenIndex = new ArrayList<Integer>();
		
		int step = 0, maxStep = g.numEdges;
		while (g.numEdges > 0 && step < maxStep) {
			int maxCount = 0, maxT = -1;
			
			// choose the template that cut the maximum number of edges
			for (int t = 0; t < numCol; t++) {
				int count = 0;
				
				for (int i = 0; i < passValues.size(); i++) {
					for (int j = 0; j < failValues.size(); j++) {
						if (matrix[i][t] == 1 && matrix[j + passSize][t] == 0 &&
								notCut(edges, i, j + passSize)) {
							count++;
						}
					}
				}
				
				if (count > maxCount) {
					maxCount = count;
					maxT = t;
				}
			}
			
			if (maxT == -1) return null;
			chosenIndex.add(maxT);
			
			// remove the edges that have been cut
			Iterator<Pair> it = edges.iterator();
			while (it.hasNext()) {
				Pair p = it.next();
				if (matrix[p.pass][maxT] == 1 && matrix[p.fail][maxT] == 0 &&
						!p.isCut) {
					p.isCut = true;
					g.numEdges--;
				}
			}
			
			step++;
		}
		
		if (g.numEdges > 0) return null;
		else return chosenIndex;
	}
	
	public boolean checkRegion(List<Integer> comb) {
		int numRow = passValues.size() + failValues.size();
		
		int passSize = passValues.size();
		
		// check for each fail value
		for (int failIndex = passSize; failIndex < numRow; failIndex++) {
			boolean satisfied = false;
			Iterator<Integer> it = comb.iterator();

			while (it.hasNext()) {
				int colIndex = it.next();
				if (matrix[failIndex][colIndex] == 0) {
					satisfied = true;
					break;
				}
			}

			// this combination is not valid
			if (!satisfied) return false;
		}
		
		return true;
	}
	
	public List<Integer> intersect(List<Integer> comb) {
		int passSize = passValues.size();
		
		List<Integer> ret = new ArrayList<Integer>();
		
		for (int i = 0; i < passSize; i++) {
			Iterator<Integer> it = comb.iterator();
			boolean cover = true;
			
			while (it.hasNext()) {
				int j = it.next();
				if (matrix[i][j] == 0 || matrix[i][j] == -1) {
					cover = false;
					break;
				}
			}
			
			if (cover) ret.add(i);
		}
		
		return ret;
	}
	
	public void combination(List<List<Integer>> combines,
			List<Integer> a, int k, int start, int currLen, boolean[] used) {
		if (currLen == k) {
			List<Integer> newCombine = new ArrayList<Integer>();
			
			for (int i = 0; i < a.size(); i++)
				if (used[i]) newCombine.add(a.get(i));
			
			combines.add(newCombine);
			return;
		}
		
		if (start == a.size()) return;
		
		used[start] = true;
		combination(combines, a, k, start + 1, currLen + 1, used);
		
		used[start] = false;
		combination(combines, a, k, start + 1, currLen, used);
	}
	
	public List<List<Integer>> combination(List<Integer> a, int k) {
		List<List<Integer>> combines = new ArrayList<List<Integer>>();
		boolean[] b = new boolean[a.size()];
		for (int i = 0; i < b.length; i++) b[i] = false;
		
		combination(combines, a, k, 0, 0, b);
		
		return combines;
	}
			
	public List<List<SingleTemplate>> groupTemplates(List<Integer> chosenIndex) {
		int numRow = passValues.size() + failValues.size();
		
		int passSize = passValues.size();
		
		List<Integer> passIndex = new ArrayList<Integer>();
		for (int i = 0; i < passSize; i++) passIndex.add(i);
		
		List<List<SingleTemplate>> disj = new ArrayList<List<SingleTemplate>>();
		
		outer:
		while (!passIndex.isEmpty()) {
			int size = 0;
			
			// the number of conj in each disj
			for (size = 1; size <= chosenIndex.size(); size++) {
				// choose the conj such that together they classify all fail values correctly
				List<List<Integer>> combs = combination(chosenIndex, size);
				
				Iterator<List<Integer>> cit = combs.iterator();
				while (cit.hasNext()) {
					List<Integer> comb = cit.next();
					
					if (checkRegion(comb)) {
						boolean improved = false;
						List<Integer> inter = intersect(comb);
						
						Iterator<Integer> it = passIndex.iterator();
						while (it.hasNext()) {
							int index = it.next();
							if (inter.contains(index)) {
								it.remove();
								improved = true;
							}
						}
						
						if (improved) {
							List<SingleTemplate> conj = new ArrayList<SingleTemplate>();
							for (int t = 0; t < comb.size(); t++)
								conj.add(primTemplates.get(comb.get(t)));
							disj.add(conj);
						}
						
						if (passIndex.isEmpty()) break outer;
					}
				}
			}
			
			if (size > chosenIndex.size()) break;
		}
		
		if (!passIndex.isEmpty()) disj = null;
				
//		// group the template that have the same pass values into conj
//		while (chosenIndex.size() > 0) {
//			List<Template> conj = new ArrayList<Template>();
//			Iterator<Integer> it = chosenIndex.iterator();
//			
//			int i1 = it.next();
//			Template t1 = templates.get(i1);
//			conj.add(t1);
//			it.remove();
//			
//			while (it.hasNext()) {
//				int i2 = it.next();
//				boolean group = true;
//				for (int i = 0; i < passSize; i++) {
//					if (matrix[i][i1] != matrix[i][i2]) {
//						group = false;
//						break;
//					}
//				}
//				
//				if (group) {
//					conj.add(templates.get(i2));
//					it.remove();
//				}
//			}
//			
//			disj.add(conj);
//		}
		
		return disj;
	}
	
	public void checkPAC() {
		if (primTemplates.isEmpty()) return;
		
		buildMatrix();
		
//		System.out.println(passValues.size());
//		
//		for (int i = 0; i < matrix.length; i++) {
//			for (int j = 0; j < matrix[0].length; j++) {
//				System.out.print(matrix[i][j] + " ");
//			}
//			
//			System.out.println();
//		}
		
		buildGraph();
		List<Integer> chosenIndex = chooseTemplatesIndex();
		
//		System.out.println(chosenIndex);
//		
//		for (int i = 0; i < matrix.length; i++) {
//			for (int j : chosenIndex) {
//				System.out.print(matrix[i][j] + " ");
//			}
//			System.out.println();
//		}
		
		// should not check regions if there are too many chosen indexes
		if (chosenIndex == null || chosenIndex.size() > 10) return;
		List<List<SingleTemplate>> disj = groupTemplates(chosenIndex);
		
		if (disj != null) validTemplates.add(new CompositeTemplate(disj));
	}

}
