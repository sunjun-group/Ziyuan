package sav.commons.testdata.assertion;
public class PrimitiveAssertion {
	public double foo(int a, int b) {
		if (a > b) {
			// assert a + b > 0;
			throw new IllegalArgumentException("Vectors should be contains the same number of elements.");
		}
		return 1;
	}
}
