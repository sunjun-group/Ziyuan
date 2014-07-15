package testdata.slice;

import java.util.ArrayList;
import java.util.Random;

public class FindMaxCaller {
	private ArrayList<Integer> testArray = new ArrayList<Integer>();

	public void execute(int seed) {
		Random random = new Random();
		// init array
		for (int i = 0; i < 10; i++) {
			testArray.add(random.nextInt(seed));
		}
		testArray.add(seed + 20);

		int max = FindMax.findMax(testArray);
		System.out.println("The maximum element of the array is " + max);
	}
}
