	public static void loop(int a, int b, int c) {
		if ((b - c >= 1) && (a == c)) {
			b = 10;
			c += 10;
			a = c;
			loop(a, b, c);
			return;
		}
		return;
	}