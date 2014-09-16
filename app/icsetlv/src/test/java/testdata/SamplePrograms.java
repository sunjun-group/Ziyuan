package testdata;

public class SamplePrograms {
	private int a = 10;
	private int b = 20;
	private int c = 30;
	
	public int max(int g, int e, int f)
	{
		int result = a;
		
		if(b > result)
		{
			int d = 0;
			d++;
			System.out.println(d);
			result = a; 
		}
		
		if(c > result)
		{
			result = c;
		}
		
		return result;
	}
}
