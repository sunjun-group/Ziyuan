package testdata.benchmark;

public class MaxValue {

	public int max(int a, int b) {
		if (a > b) {
			return a;
		}
		return b;
	}
	
	public static void main(String[] args) {
		new MaxValue().max(3, 19);
	}
}
