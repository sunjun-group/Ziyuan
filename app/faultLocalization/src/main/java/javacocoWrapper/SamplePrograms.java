package javacocoWrapper;

public class SamplePrograms {
	public int Max(int a, int b, int c)
	{
		int result = a;
		
		if(result > b)
		{
			//result = b;
			result = a; //wrong assignment
		}
		else if(result > c)
		{
			result = c;
		}
		
		return c;
	}
}
