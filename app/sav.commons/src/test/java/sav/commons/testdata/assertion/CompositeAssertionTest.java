package sav.commons.testdata.assertion;
public class CompositeAssertionTest {
	public double foo(int a, int b) {
		assert a >= 0;
		assert b >= 0;
		return 1.0;
		// a>=0 && b>=0
	}
}
