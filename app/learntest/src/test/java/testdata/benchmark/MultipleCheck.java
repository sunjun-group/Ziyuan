package testdata.benchmark;

public class MultipleCheck {

	public void check(int a, int b, int c) {
		if (a > b + c) {
			System.out.println("a > b + c");
		} else if (a > b - c) {
			System.out.println("a <= b + c && a > b - c");
		}
	}
}
