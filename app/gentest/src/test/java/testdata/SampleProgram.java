/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata;

/**
 * @author LLT
 *
 */
public class SampleProgram {
	
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

	/**
	 * evaluation method.
	 */
	public static boolean checkMax(int a, int b, int c, int result) {
		if (a > result || b > result || c > result) {
			return false;
		}
		return true;
	}
}
