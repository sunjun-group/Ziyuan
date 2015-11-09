package sav.commons.testdata.assertion;
public class MultipleAssertionsTest {
	public double foo(int a, int b) {
		if (a > b) {
			return Math.sqrt(a);
		} else {
			return Math.sqrt(b);
		}
	}
}
