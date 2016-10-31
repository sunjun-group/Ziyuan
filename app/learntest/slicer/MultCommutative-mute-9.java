	public int mult(int n, int m) {
		if (m < 0) {
			return mult(n, -m);
		}
		if (m == 0) {
			return 0;
		}
		return n + mult(n, m - 1);
	}