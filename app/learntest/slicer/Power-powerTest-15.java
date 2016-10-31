	public int powerTest(int base, int exp) {
		if (exp < 0) {
			return -1;
		}

		if (exp == 0) {
			return 1; // n^0 = 1
		}
		
		//to achieve 100% coverage
		if (exp == 1) {
			return base;
		}
		return  0;
	}