package testdata.numeric;

public class MultCommutative {

	public int mult(int n, int m) {
		if (m < 0) {
			return mult(n, -m);
		}
		if (m == 0) {
			return 0;
		}
		return n + mult(n, m - 1);
	}
	
	public static void main(String[] args) {
		new MultCommutative().mult(4, 5);
	}

}
