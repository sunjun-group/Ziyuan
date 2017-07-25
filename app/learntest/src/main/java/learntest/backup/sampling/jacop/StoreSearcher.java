package learntest.backup.sampling.jacop;

import java.util.ArrayList;
import java.util.List;

import org.jacop.core.Domain;
import org.jacop.core.Store;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatVar;
import org.jacop.floats.search.SplitSelectFloat;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.Search;

import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;

public class StoreSearcher {
	
	public static int length;

	public static List<Domain[]> solve(List<Store> stores) throws SAVExecutionTimeOutException {
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
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		Search<FloatVar> search = new DepthFirstSearch<FloatVar>();
		//search.setSolutionListener(new PrintOutListener<FloatVar>());
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SplitSelectFloat<FloatVar> select = new SplitSelectFloat<FloatVar>(store, intVars, null);
	    boolean result = search.labeling(store, select);
	    if (result) {
			return search.getSolution();
		}
	    return null;
	}
	
	public static Domain[] minSolve(Store store) {
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		Search<FloatVar> search = new DepthFirstSearch<FloatVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SplitSelectFloat<FloatVar> select = new SplitSelectFloat<FloatVar>(store, intVars, null); 
	    boolean result = search.labeling(store, select);
	    if (result) {
			return search.getSolution();
		}
	    return null;
	}
	
	public static Domain[] maxSolve(Store store) {
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		Search<FloatVar> search = new DepthFirstSearch<FloatVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SplitSelectFloat<FloatVar> select = new SplitSelectFloat<FloatVar>(store, intVars, null);
	    boolean result = search.labeling(store, select);
	    if (result) {
			return search.getSolution();
		}
	    return null;
	}
	
	public static List<Domain[]> solveAll(List<Store> stores) throws SAVExecutionTimeOutException {
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
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		Search<FloatVar> search = new DepthFirstSearch<FloatVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SplitSelectFloat<FloatVar> select = new SplitSelectFloat<FloatVar>(store, intVars, null);
	    boolean result = search.labeling(store, select);
	    if (result) {
	    	int cnt = search.getSolutionListener().solutionsNo();
    		for (int i = 1; i <= cnt; i++) {
				res.add(search.getSolution(i));
    		}
				
		}
		return res;
	}
	
	public static List<Domain[]> solve(List<Store> stores, int number) throws SAVExecutionTimeOutException {
		List<Domain[]> res = new ArrayList<Domain[]>();
		number /= stores.size();
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
		FloatVar[] intVars = new FloatVar[store.size()];
		for (int i = 0; i < intVars.length; i++) {
			intVars[i] = (FloatVar)store.vars[i];
		}
		Search<FloatVar> search = new DepthFirstSearch<FloatVar>();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setTimeOut(1);
		search.setPrintInfo(false);
		SplitSelectFloat<FloatVar> select = new SplitSelectFloat<FloatVar>(store, intVars, null);
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
	
	public static boolean duplicate(Domain[] s1, Domain[] s2) throws SAVExecutionTimeOutException {
		if (s1 == null && s2 == null) {
			return true;
		} else if (s1 == null || s2 == null) {
			return false;
		}
		if (s1.length != s2.length) {
			return false;
		}
		if (s1.length < length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if(SAVTimer.isTimeOut()){
				throw new SAVExecutionTimeOutException("Time out in StoreSearchr.duplicate()");
			}
			
			if (((FloatDomain) s1[i]).min() != ((FloatDomain) s2[i]).min()) {
				return false;
			}
		}
		return true;
	}
}
