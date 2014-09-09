package testdata;

public class SamplePrograms {
	
	public static int max(int a, int b, int c)
	{
		int result = a;
		
		if(b > result)
		{
			//result = b;
			int d = 0;
			d++;
			System.out.println(d);
			result = a; //wrong assignment
		}
		
		if(c > result)
		{
			result = c;
		}
		
//		System.out.println(result);
		return result;
	}
}
