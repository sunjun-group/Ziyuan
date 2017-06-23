package testdata.benchmark;

public class Complex {

	public void complexCheck(int a, int b, int c, int d, int e) {
		if (a < 3 && b > 3 && c > 5) {
			System.out.println("complex if executed");
		}
		else if (a > 3 && b > 3 && c > 5 && (d > 6 && d < 8)) {
			System.out.println("complex else if executed");
			if (c + b < 12 && a + b < 15 && a > 5) {
				System.out.println("the inner if executed");
				if (d + e > -1 && d + e < 1) {
					System.out.println("Target reached");
				}
			}			
		} else {
			System.out.println("the false branch taken");
		}
	}
	
}
