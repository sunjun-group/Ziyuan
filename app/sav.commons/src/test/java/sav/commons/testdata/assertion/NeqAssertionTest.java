package sav.commons.testdata.assertion;
public class NeqAssertionTest {
	public double foo(int n) {
		int result = 0;
	    while (n > 0) {
	      result += (n & 1) == 1 ? 1 : 0;
	      n = n >> 1;
	    }
	    return result;
	}
}