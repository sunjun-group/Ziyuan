/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata;

/**
 * @author khanh
 *
 */
public class FindMax implements IFindMax{
	private int a ;
	private int b;
	private int c;
	
	public FindMax(int a, int b, int c){
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public int Max()
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

	public boolean check(int result){
		if (a > result || b > result || c > result) {
			return false;
		}
		return true;
	}
}
