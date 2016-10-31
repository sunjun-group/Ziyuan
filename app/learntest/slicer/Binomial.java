package testdata.numeric;

public class Binomial {

	public static int fact(int n) {
		if (n <= 0)
			return 1;
		else
			return n * fact(n - 1);
	}

	public static int binomialCoefficient(int n, int k) {
		return fact(n) / (fact(k) * fact(n - k));
	}

	public static void main(String args[]) {
		for (int n = 0; n <= args.length; n++)
			for (int k = 0; k <= args.length; k++)
				if (k <= n)
					binomialCoefficient(n, k);
				else
					binomialCoefficient(k, n);
	}

}
