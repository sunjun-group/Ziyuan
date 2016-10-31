package testdata.benchmark;

public class Fibonacci {

	public int fibonacciTest(int number) {

		if (number < 0) {
			return -1;
		}

		switch (number) {
		case 0:
			return 0;
		case 1:
			return 1;
		default: {
			int[] fibs = new int[number + 1];
			fibs[0] = 0;
			fibs[1] = 1;

			// populate fibs with fib sequence
			for (int i = 2; i <= number; i++) {
				fibs[i] = fibs[i - 1] + fibs[i - 2];
			}

			return fibs[number];
		}
		}

	}

	public static void main(String[] args) {
		new Fibonacci().fibonacciTest(12);
	}
}
