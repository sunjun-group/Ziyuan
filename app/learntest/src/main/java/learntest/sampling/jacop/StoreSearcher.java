package learntest.sampling.jacop;

import java.util.ArrayList;
import java.util.List;

import org.jacop.core.Domain;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMax;
import org.jacop.search.IndomainMin;
import org.jacop.search.IndomainRandom;
import org.jacop.search.InputOrderSelect;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;

public class StoreSearcher {
	
	public static List<Domain[]> solve(List<Store> stores) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		for (Store store : stores) {
			Domain[] solution = solve(store);
			if (solution != null) {
				boolean flag = true;
				for (Domain[] domains : res) {
					if (duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					res.add(solution);
				}				
			}
		}
		return res;
	}
	
	public static Domain[] solve(Store store) {
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(
				store, intVars, new IndomainRandom<IntVar>()); 
	    boolean result = search.labeling(store, select);
	    if (result) {
			return search.getSolution();
		}
	    return null;
	}
	
	public static Domain[] minSolve(Store store) {
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(
				store, intVars, new IndomainMin<IntVar>()); 
	    boolean result = search.labeling(store, select);
	    if (result) {
			return search.getSolution();
		}
	    return null;
	}
	
	public static Domain[] maxSolve(Store store) {
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(
				store, intVars, new IndomainMax<IntVar>()); 
	    boolean result = search.labeling(store, select);
	    if (result) {
			return search.getSolution();
		}
	    return null;
	}
	
	public static List<Domain[]> solveAll(List<Store> stores) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		for (Store store : stores) {
			List<Domain[]> solutions = solveAll(store);
			for (Domain[] solution : solutions) {
				boolean flag = true;
				for (Domain[] domains : res) {
					if (duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					res.add(solution);
				}
			}
		}
		return res;
	}
	
	public static List<Domain[]> solveAll(Store store) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		if (store == null) {
			return res;
		}
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(
				store, intVars, new IndomainRandom<IntVar>()); 
	    boolean result = search.labeling(store, select);
	    if (result) {
	    	int cnt = search.getSolutionListener().solutionsNo();
    		for (int i = 1; i <= cnt; i++) {
				res.add(search.getSolution(i));
    		}
				
		}
		return res;
	}
	
	public static List<Domain[]> solve(List<Store> stores, int number) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		for (Store store : stores) {
			List<Domain[]> solutions = solve(store, number);
			for (Domain[] solution : solutions) {
				boolean flag = true;
				for (Domain[] domains : res) {
					if (duplicate(domains, solution)) {
						flag = false;
						break;
					}
				}
				if (flag) {
					res.add(solution);
				}
			}
		}
		return res;
	}

	public static List<Domain[]> solve(Store store, int number) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		if (store == null) {
			return res;
		}
		IntVar[] intVars = new IntVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (IntVar)store.vars[i];
		}
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(
				store, intVars, new IndomainRandom<IntVar>()); 
	    boolean result = search.labeling(store, select);
	    if (result) {
	    	int cnt = search.getSolutionListener().solutionsNo();
	    	if (cnt <= number) {
	    		for (int i = 1; i <= cnt; i++) {
					res.add(search.getSolution(i));
				}
			} else {
				int times = cnt / number;
				int idx = times;
				for (int i = 0; i < number; i++) {
					res.add(search.getSolution(idx));
					idx += times;
				}
			}			
		}
		return res;
	}
	
	public static boolean duplicate(Domain[] s1, Domain[] s2) {
		if (s1 == null && s2 == null) {
			return true;
		} else if (s1 == null || s2 == null) {
			return false;
		}
		if (s1.length != s2.length) {
			return false;
		}
		for (int i = 0; i < s1.length; i++) {
			if (((IntDomain) s1[i]).min() != ((IntDomain) s2[i]).min()) {
				return false;
			}
		}
		return true;
	}
}
