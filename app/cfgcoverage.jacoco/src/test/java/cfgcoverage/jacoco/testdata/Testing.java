package cfgcoverage.jacoco.testdata;

public class Testing {

	public void test(double x, double y) {
		if (x > 6) {
			if (x < 10) {
				if (x > 8) {
					System.out.println();//
				} else {
					return;//
				}
			} else {
				System.out.println();//
			}
		} else {
			System.out.println();//
		}
		if (x != y) {
			return;//
		}
		if (x != x) {
			return;
		} 
		return;//
	}
	
	public void multiCond(double x, double y) {
		if (x > 6) {
			if (x < 10) {
				if (x > 20 || y == 1) {
				} else
					return;
			}
		}
		if (x != y) {
			return;
		}
		if (x != x) {
			return;
		}
	}

	public void method1(double x, double y) {
		if (x > 6) {
			if (x < 10) {
				if (x > 8) {
				} else
					return;
			}
		}
		if (x != y) {
			return;
		}
		if (x != x) {
			return;
		}
	}

 	public void method2(double x, double y) {
		if (x > 6) {
			if (x < 10) {
				if (x > 8) {
				} else
					return;
			}
		}
		if (x != y) {
			return;
		}
		if (x != x) {
			return;
		}
	}
}
