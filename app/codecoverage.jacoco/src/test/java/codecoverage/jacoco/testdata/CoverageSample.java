package codecoverage.jacoco.testdata;

public class CoverageSample {
	
	public void method1(int a, int b) {
		check(a);
		System.out.println(a);
		try {
			check(b);
		} catch (RuntimeException e) {
			throw e;
		}
		System.out.println(b);
		System.out.println("Sum = " + (a + b));
	}

	private void check(int val) {
		if (val == 0) {
			throw new IllegalArgumentException();
		}
	}
	
}
