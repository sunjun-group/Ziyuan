package faultLocaliation.sample;

public class SamplePrograms {
	
	public boolean Check(int a, int b, int c, int result){
		return (result >= a && result >= b && result >= c && (result == a || result == b || result == c));
	}
	
	public void test() {
		int a = 1;
		int b = 2;
		int c = 3;
		
	}
	
	public int Max(int a, int b, int c)
	{
		int result = a;
		int x = 0;
		
		if(b > result)
		{
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

}
