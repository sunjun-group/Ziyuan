/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata.calculator;

/**
 * @author khanh
 *
 */
public class Sum {
	private int a;
	private int b;
	public Sum(int a, int b){
		this.a = a;
		this.b = b;
	}
	
	public int getSum() {
		if(a > 3){
			a++;
		}
		
		return a + b;
	}
	
	public static boolean validateGetSum(int a, int b, int max) {
		return (max == a + b);
	}
	
}
