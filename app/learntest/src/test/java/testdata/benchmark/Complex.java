package testdata.benchmark;

public class Complex {

	public void complexCheck(int a, int b, int c, int d, int e) {
		if (a < 10 && b > 1000 && c >999) {
			System.out.println("complex if executed");
		}
		else if (a > 10 && b > 1000 && c > 999 && (d > 999 && d < 1001)) {
			System.out.println("complex else if executed");
			if (c + b < 2003 && a + b < 1400 && a > 390) {
				System.out.println("the inner if executed");
				if (d + e > 2001 && d + e < 2003) {
					System.out.println("Target reached");
				}
			}			
		} else {
			System.out.println("the false branch taken");
		}
	}
	
}
