package sav.commons.testdata.assertion;
public class MultipleMethodsTest {
	public double foo(int a, int b) {
		int i = foo2(a);
		// assert i + b > 0;
		return i;
	}
	
	public int foo2(int a) {
		assert a > 0;
		return a;
	}
}