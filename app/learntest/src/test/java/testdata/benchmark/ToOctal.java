package testdata.benchmark;

public class ToOctal {

	public int toOctalTest(int value) {
		if (value < 0) {
			return -1;
		}
		
		//to achieve 100% coverage
		if (value == 0) {
			return 0;
		}

		StringBuilder sb = new StringBuilder();
		while (value > 0) {
			sb.append(value % 8);
			value /= 8;
		}
		
		return Integer.parseInt(
			sb.reverse().toString());
	}
	
	public static void main(String[] args) {
		new ToOctal().toOctalTest(134);
	}
}
