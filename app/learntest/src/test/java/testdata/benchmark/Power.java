package testdata.benchmark;

public class Power {

	public int powerTest(int base, int exp) {
		if (exp < 0) {
			return -1;
		}

		if (exp == 0) {
			return 1; // n^0 = 1
		}

		int power = base;
		while (exp > 1) {
			power *= base;
			exp--;
		}

		return power;
	}
	
	public static void main(String[] args) {
		new Power().powerTest(4, 3);
	}
}
