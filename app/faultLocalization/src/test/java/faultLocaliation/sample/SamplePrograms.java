package faultLocaliation.sample;

public class SamplePrograms {
	
	
	public int Max(int a, int b, int c)
	{
		int result = a;
		
		if(b > result)
		{
			//result = b;
			result = a; //wrong assignment
		}
		
		if(c > result)
		{
			result = c;
		}
		return result;
	}
}
