package sav.commons.testdata.calculator;

public class Calculator {

	public static int getMax(int x, int y) {
		int max = -x; // should be max = x
		if (max < y) {
			max = y;
		}
		if (x * y < 0) {
			System.out.println("diff.sign");
		}
		System.out.println(max);
		return max;
	}
	
	public static boolean validateGetMax(int x, int y, int max) {
		return max >= x && max >= y && (max == x || max == y);
	}

}
