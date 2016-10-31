	public int isEven(int n) {
		if (n < 0) {
			n = -n;
		}
		if (n == 0) {
			return 1;
		} else if (n == 1) {
			return 0;
		} else {
			return isOdd(n - 1);
		}
	}