	public int addition(int m, int n) {
	    if (n > 0) {
	        return addition(m+1, n-1);
	    }
	    if (n < 0) {
	        return addition(m-1, n+1);
	    }
	    return m;
	}