package testdata.benchmark;

public class Abs {
	
	public int getAbs(int x) {
		if (x < 0) {
			x = -x;
		}
		return x;
	}

	public static void main(String[] args) {
		new Abs().getAbs(1);
	}
}
