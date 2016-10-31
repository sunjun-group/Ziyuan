package testdata.numeric;

public class Et1 {

	public static void loop(int a, int b) {
		if (a <= b) {
			return;
		}
		b = b + a;
		a = a + 1;
		loop(a, b);
		return;
	}
	
	public static void main(String[] args) {
		Et1.loop(3, 2);
	}

}
