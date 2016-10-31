	public static int fact(int x) {
		 if (x > 1) {
			 int y = fact(x - 1);
			 return y * x;
		 }
		 return 1;
	 }