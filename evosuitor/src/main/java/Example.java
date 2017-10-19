public class Example {

	public static void main(String[] args) throws Exception {
		Example ex = new Example();
		ex.foo(2, 1);
	}

	public int foo(int x, int y) {
		int z = x + y;
		if (z > 0) {
			z = 1;
		}
		if (x < 5) {
			z = -z;
		} else if (x < 4) {
			z++;
		}
		return z;
	}

}