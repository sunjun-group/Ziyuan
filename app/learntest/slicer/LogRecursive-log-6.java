	public static int log(int x, int y) {
		if (x >= y && y > 1) {
			return 1 + log(x / y, y);
		}
		return 0;
	}