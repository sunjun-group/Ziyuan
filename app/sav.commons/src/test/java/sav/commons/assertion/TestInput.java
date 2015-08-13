package sav.commons.assertion;
public class TestInput {
	public double foo(int a, int b) {
		int r = a;
		r = r + b;
		// return 1 / r; // a + b = 0
		// assert r >= 0
		return Math.sqrt(r); 
	}
}
