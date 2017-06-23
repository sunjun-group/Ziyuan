package sav.commons.testdata.assertion;
public class MultipleAssertionsTest {
	public double foo(int a, int b) {
		if (a > b) {
			assert a >= 0;
			return Math.sqrt(a);
		} else {
			assert b >= 0;
			return Math.sqrt(b);
		}
	}
}
