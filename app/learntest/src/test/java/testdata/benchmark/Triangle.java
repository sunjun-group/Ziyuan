package testdata.benchmark;

public class Triangle {

	public void triangleType(int x, int y, int z) {
		if (x <= 0 || y <= 0 || z <= 0) {
			System.out.println("Invalid inputs: negative or zero side");
		}
		else if (x > y + z || y > x + z || z > x + y) {
			System.out.println("Invalid inputs: sum of two sides is less than the third side");
		}
		else if (x == y && y == z) {
			System.out.println("an equilateral");
		}
		else if (x == y || y == z || z == x) {
			System.out.println("an isoceles");
		}
		else {
			System.out.println("a scalene");
		}
	}
	
	public static void main(String[] args) {
		new Triangle().triangleType(2, 3, 4);
	}
}
