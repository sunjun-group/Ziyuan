package testdata.numeric;

public class Et4 {

	public static void loop(int a, int b, int c) {
		if ((b - c >= 1) && (a == c)) {
			b = 10;
			c += 10;
			a = c;
			loop(a, b, c);
			return;
		}
		return;
	}
	
	public static void main(String[] args) {
		Et4.loop(1, 1, 1);
	}

}
