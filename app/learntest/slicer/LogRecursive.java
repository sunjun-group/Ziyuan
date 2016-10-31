package testdata.numeric;

public class LogRecursive {

	public static int log(int x, int y) {
		if (x >= y && y > 1) {
			return 1 + log(x / y, y);
		}
		return 0;
	}
	
	public static void main(String[] args) {
		LogRecursive.log(4, 9);
	}
	
}
