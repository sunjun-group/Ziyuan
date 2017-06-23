package testdata.benchmark;

public class Check {

	public void check(int a, int b, int c) {
		if(a > b) {
			System.out.println("a > b");
		}
		if (a > c) {
			System.out.println("a > c");
		}
		if(b > c) {
			System.out.println("b > c");
		}
	}
	
}
