package testdata;

public class MutipleSelection {
	
	public static void test(int x, int y) {
		if (x > 10) {
			while (x > y) {
				x --;
			}
		}
		else if (y < 10) {
			y ++;
		}
		if (x > 10) {
			while (x != y) {
				if (x > y) {
					x --;
				} else {
					x ++;
				}
			}
		}
	}

}
