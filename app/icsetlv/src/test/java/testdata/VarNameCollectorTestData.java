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
public class VarNameCollectorTestData {
	private int a;
	private InnerClass innerClass;

	public VarNameCollectorTestData(int a) {
		this.a = a;
		innerClass = new InnerClass();
		innerClass.b = 2;
		innerClass.inner = new InnerClass();
		innerClass.inner.b = 5;
	}

	public int getSum(int x, int y) {
		if (a + innerClass.b > 3) {
			x++;
		}

		return x + y;
	}

	public static boolean validateGetSum(int x, int y, int max) {
		return (max == x + y);
	}
	
	public static void loop(InnerClass[] var) {
		int max = Integer.MIN_VALUE;
		int i = 0;
		for (; i < var.length - 1; i++) {
			if (max < var[i].b) {
				max = var[i].b;
			}
		}
	}

	class InnerClass {
		InnerClass inner;
		int b;
	}
}
