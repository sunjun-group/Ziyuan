package testdata.benchmark;

public class Middle {

	public int findMiddle(int a, int b, int c) {
		if ((a < b && b < c) || (c < b && b < a)) {
			System.out.println("middle value is b = " + b);
			return b;
		}
		else if ((a < c && c < b) || (b < c && c < a)) {
			System.out.println("middle value is c = " + c);
			return c;
		}
		else if ((b < a && a < c) || (c < a && a < b)) {
			System.out.println("middle value is a = " + a);
			return a;
		}
		else if (b == c) {
			System.out.println("b is equal to c, middle value is" + a);
			return a;
		}
		else if (b == a) {
			System.out.println("b is equal to a, middle value is" + c);
			return c;
		}
		else {
			System.out.println("a is equal to c");
			return b;
		}
	}
	
}
