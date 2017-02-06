package testdata.benchmark;

public class Quadratic {

	public void quad(int a, int b, int c) {
		if (a == 0) {
			System.out.println("not a quadratic equation");
		}
		else if (b * b - 4 * a * c > 0) {
			System.out.println("Roots are real and unequal");
		}
		else if (b * b - 4 * a * c == 0) {
			System.out.println("Roots are real and equal");
		}
		else {
			System.out.println("Roots are complex");
		}
	}
	
	public static void main(String[] args) {
		new Quadratic().quad(1, 2, 3);
	}
	
}
