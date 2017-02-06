package testdata.benchmark;

public class Factorial {

	public int factorialTest(int number) {
		if (number < 0) {
			return -1;
		}

		if (number < 2) {
			return 1;
		}

		int factorial = 1;
		for (int i = 2; i <= number; i++) {
			factorial *= i;
		}

		return factorial;
	}
	
	public static void main(String[] args) {
		new Factorial().factorialTest(4);
	}
}
