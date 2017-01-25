package datastructure;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

	public static <E> List<List<E>> perm(List<E> orig) {
		if (orig.size() == 0) {
			List<List<E>> result = new ArrayList<List<E>>();
			result.add(new ArrayList<E>());
			
			return result;
		}
		
		E var = orig.remove(0);
		List<List<E>> result = new ArrayList<List<E>>();
		
		List<List<E>> perms = perm(orig);
		
		for (List<E> perm : perms) {
			for (int i = 0; i <= perm.size(); i++) {
				List<E> tmp = new ArrayList<E>(perm);
				tmp.add(i, var);
				
				result.add(tmp);
			}
		}
		
		return result;
	}
	
	public static <E> List<List<E>> comb(List<E> orig, int k) {
		List<List<E>> comb = new ArrayList<List<E>>();
		List<E> curr = new ArrayList<E>();
		
		if (orig.size() < k) return comb;
		
		comb(comb, curr, orig, k, 0);
		
		return comb;
	}
	
	private static <E> void comb(List<List<E>> acc, List<E> curr,
			List<E> orig, int k, int index) {
		if (k == 0) {
			List<E> vl = new ArrayList<E>(curr);
			acc.add(vl);
		} else {
			for (int i = index; i < orig.size() - k + 1; i++) {
				curr.add(orig.get(i));
				comb(acc, curr, orig, k - 1, i + 1);
				curr.remove(orig.get(i));
			}
		}
	}
	
}
