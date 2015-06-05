/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata.testcasesexecutor.test1;

import java.util.Random;


/**
 * @author LLT
 *
 */
public class TcExSum {
	private int a;
	private InnerClass innerClass;
	public TcExSum(int a){
		this.a = a;
		innerClass = new InnerClass();
		innerClass.b = 2;
		innerClass.inner = new InnerClass();
		innerClass.inner.b = 5;
		innerClass.a = new Random().nextInt(5);
	}
	
	public int getSum(int x, int y) {
		int a = 5;
		if(a + this.a + innerClass.b + innerClass.a > 50){
			x++;
		}
		
		return x+y;
	}
	
	public static boolean validateGetSum(int x, int y, int max) {
		return (max == x + y);
	}
	
	class InnerClass{
		InnerClass inner;
		int a;
		int b;
	}
}
