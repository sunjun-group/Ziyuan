package cfgcoverage.jacoco.testdata;

import sav.common.core.SavRtException;

public class InterproceduralSample {
	
	public int Max(int a, int b, int c)
	{
		int result = a;
		int x = 0;
		
		if(b > result)
		{
			//result = b;
			x += 6;
			method1(b, c);
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
		return true;
	}
	
	private void method1(int x, int y) {
		if (x == 0) {
			throw new IllegalArgumentException();
		} else if (y == 0) {
			throw new SavRtException("");
		}
		System.out.println();
	}
}
