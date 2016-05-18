package testdata.benchmark;

public class Abs {
	
	public int getAbs(int x) {
		if (x < 0) {
			x = -x;
		}
		return x;
	}

}
