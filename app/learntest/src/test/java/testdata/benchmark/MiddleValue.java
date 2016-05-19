package testdata.benchmark;

public class MiddleValue {

	public int middle(int a, int b, int c) {
		int max = Math.max(a, b);
		if (c > max) {
			max = c;
		}
		if (a == max) {
			if (b > c) {
				return b;
			}
			return c;
		}
		if (b == max) {
			if (a > c) {
				return a;
			}
			return c;
		}
		if (a > b) {
			return a;
		}
		return b;
	}
	
}
