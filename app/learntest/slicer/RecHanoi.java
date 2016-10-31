package testdata.numeric;

public class RecHanoi {

	public int hanoi(int n) {
		if (n < 1 || n > 31) {
	    	return 0;
	    }
	    if (n == 1) {
			return 1;
		}
		return 2 * (hanoi(n-1)) + 1;
	}
	
	public static void main(String[] args) {
		new RecHanoi().hanoi(4);
	}
	
}
