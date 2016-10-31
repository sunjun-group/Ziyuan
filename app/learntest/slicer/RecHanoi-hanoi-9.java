	public int hanoi(int n) {
		if (n < 1 || n > 31) {
	    	return 0;
	    }
	    if (n == 1) {
			return 1;
		}
		return 2 * (hanoi(n-1)) + 1;
	}