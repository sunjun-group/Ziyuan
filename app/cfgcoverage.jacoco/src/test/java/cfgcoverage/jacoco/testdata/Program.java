package cfgcoverage.jacoco.testdata;

public class Program {
	
	public int Max(int a, int b, int c)
	{
		int result = a;
		int x = 0;
		if(b > result)
		{
			for (int i = 0; i <= 5; i++) {
				System.out.println(i);
			}
			//result = b;
			x += 6;
			x *= 3;
			result = a; //wrong assignment
		}
		
		if(c > result)
		{
			System.out.println(x);
			x += 7;
			System.out.println(x);
			result = c;
		}
		return result;
	}

	public static boolean checkMax(int a, int b, int c, int result) {
		if (a > result || b > result || c > result) {
			return false;
		}
		if (a > b) {
			
		}
		return true;
	}
	
	public void whiletrue() {
		while(true) {
			System.out.println("inloop");
		}
	}
}
