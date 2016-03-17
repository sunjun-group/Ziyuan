package testdata;

public class MutipleSelection {
	
	public static void test(int x, int y) {
		if (x > 10) {
			x --;
		} 
		else if (y < 10) {
			y ++;
		}
		while (x > y) {
			x --;
		}
	}

}
