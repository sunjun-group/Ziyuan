package testdata.numeric;

public class Addition01_true_termination {
	
	public int addition(int m, int n) {
	    if (n > 0) {
	        return addition(m+1, n-1);
	    }
	    if (n < 0) {
	        return addition(m-1, n+1);
	    }
	    return m;
	}


	public int main2(int m, int n) {
	    if (m <= 0) {
	        return 0;
	    }
	    if (n <= 0) {
	        return 0;
	    }
	    return addition(m,n);
	}
	
	public static void main(String[] args) {
		new Addition01_true_termination().main2(2, 3);
	}

}
