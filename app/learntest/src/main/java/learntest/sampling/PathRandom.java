package learntest.sampling;

import java.util.List;
import java.util.Random;

import libsvm.core.Divider;

public class PathRandom {
	
	private static Random random = new Random();
	
	public static List<Divider> dividers;
	public static List<Divider> notDividers;
	
	public static void randomPath(List<Divider> dividers, List<Divider> notDividers) {
		if (dividers == null && notDividers == null) {
			PathRandom.dividers = null;
			PathRandom.notDividers = null;
			return;
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
			PathRandom.notDividers = notDividers;
		} else {
			base = notDividers;
			PathRandom.dividers = dividers;
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
			PathRandom.dividers = base;
		} else {
			PathRandom.notDividers = base;
		}
	}

}
