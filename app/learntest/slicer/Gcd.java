package testdata.numeric;

public class Gcd {
	
	public int gcd(int y1, int y2) {
	    if (y1 <= 0 || y2 <= 0) {
	        return 0;
	    }
	    if (y1 == y2) {
	        return y1;
	    }
	    if (y1 > y2) {
	        return gcd(y1 - y2, y2);
	    }
	    return gcd(y1, y2 - y1);
	}

	public int main(int m , int n) {
	    if (m <= 0) {
	        return 0;
	    }
	    if (n <= 0) {
	        return 0;
	    }
	    int z = gcd(m, n);
	    if (z < 1 && m > 0 && n > 0) {
	        return -1;
	    } else {
	        return 0;
	    }
	}
	
	public static void main(String[] args) {
		new Gcd().gcd(2, 3);
	}

}
