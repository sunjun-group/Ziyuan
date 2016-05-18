package testdata.benchmark;

public class WrapRoundCounter {

	public int wrap_int(int n) {
		if (n > 10) {
			n = 0;
			return n;
		} else {
			n = n + 1;
			return n;
		}
	}
}
