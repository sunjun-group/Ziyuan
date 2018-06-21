package cfgcoverage.jacoco.testdata;

/**
 * @author LLT
 *
 */
public class ThrowSample {
	int[] arr = new int[10];
	
	public void run(int a, int b) {
		if ((a == 1)) {
			if (b > 0) {
				System.out.println();
				if (b < 3) {
					turnOnFlag(b);
				}
			}
		}
		System.out.println();
	}
	
	public void turnOnFlag(int b) throws IllegalArgumentException {
		try {
			arr[b] = 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}
}
