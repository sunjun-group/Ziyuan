package testdata.test;

import java.util.ArrayList;

public class FindMax {

	public static int findMax(ArrayList<Integer> arr) {
		int max = arr.get(0);
		for (int i = 1; i < arr.size() - 1; i++) {
			if (max < arr.get(i)) {
				max = arr.get(i);
			}
		}
		assertTrue(max);
		return max;
	}

	private static void assertTrue(int max) {
		assert max > 110;
	}
}
