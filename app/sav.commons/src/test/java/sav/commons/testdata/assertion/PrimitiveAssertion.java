package sav.commons.testdata.assertion;
public class PrimitiveAssertion {
	public double foo(int[] arr) {
		if (arr == null || arr.length < 1) {
//			// assert a + b > 0;
			throw new IllegalArgumentException("Vectors should be contains the same number of elements.");
		}
		return 1;
	}
}
