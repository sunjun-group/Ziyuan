package testdata.slice;

import java.util.List;

public class FindMax {

	public static int findMax(List<Integer> arr) {
		int max = arr.get(0);
		for (int i = 1; i < arr.size() - 1; i++) {
			if (max < arr.get(i)) {
				max = arr.get(i);
			}
		}
		return max;
	}
}
