package learntest.sampling;

import java.util.List;
import java.util.Random;

import libsvm.core.Divider;

/**
 * This class is used to randomly remove some constraints in dividers or non-dividers
 * @author Gao Donjing
 *
 */
public class PathRandom {
	
	private static Random random = new Random();
	
	private List<Divider> dividers;
	private List<Divider> notDividers;
	
	public static PathRandom randomPath(List<Divider> dividers, List<Divider> notDividers) {
		PathRandom pathRandom = new PathRandom();
		if (dividers == null && notDividers == null) {
			pathRandom.dividers = null;
			pathRandom.notDividers = null;
			return pathRandom;
		}
		List<Divider> base = null;
		boolean flag = random.nextBoolean();
		if (notDividers == null) {
			flag = true;
		} else if (dividers == null) {
			flag = false;
		}
		if (flag) {
			base = dividers;
			pathRandom.notDividers = notDividers;
		} else {
			base = notDividers;
			pathRandom.dividers = dividers;
		}
		int n = base.size();
		if (n > 0) {
			int times = (int) Math.pow(2, n);
			int choice = random.nextInt(times - 1);
			times /= 2;
			int sum = 0;
			for (int i = n; i > 0; i --) {
				sum += times;
				if (choice < sum) {
					base = base.subList(0, i);
					break;
				}
				times /= 2;
			}
		}		
		if (flag) {
			pathRandom.dividers = base;
		} else {
			pathRandom.notDividers = base;
		}
		return pathRandom;
	}

	public List<Divider> getDividers() {
		return dividers;
	}

	public void setDividers(List<Divider> dividers) {
		this.dividers = dividers;
	}

	public List<Divider> getNotDividers() {
		return notDividers;
	}

	public void setNotDividers(List<Divider> notDividers) {
		this.notDividers = notDividers;
	}
}
