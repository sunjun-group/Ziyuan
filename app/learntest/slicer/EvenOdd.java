package testdata.numeric;

public class EvenOdd {

	public int isOdd(int n) {
		if (n < 0) {
			n = -n;
		}
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		} else {
			return isEven(n - 1);
		}
	}

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
	
	public int main(int n) {
	    if (n < 0) {
	        return 0;
	    }
	    int result = isOdd(n);
	    if (result >= 0) {
	        return 0;
	    } else {
	        return -1;
	    }
	}
	
	public static void main(String[] args) {
		new EvenOdd().isEven(2);
	}

}
