/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.testdata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
/**
 * @author LLT
 *
 */
public class SliceCollectorTestdata {
	
	private int a;
	private InnerClass innerClass;
	
	public SliceCollectorTestdata() {
	}

	public void setA(int a) {
		this.a = a; 
		innerClass = new InnerClass(); System.out.println("n"); innerClass.b = this.a = a;
		innerClass.inner = new InnerClass();
		innerClass.inner.b = innerClass.b;
		int n = 3;
		innerClass.b = n;
	}

	public int getSum(int x, int y) {
		int[] arr = new int[] {innerClass.inner.b, a};
		arr[1] ++;
		a++;
		if (a + arr[innerClass.b - a + 1] > 3) {
			int n = 0;
			System.out.println(n);
			x++;
		}

		return x + y;
	}

	class InnerClass {
		InnerClass inner;
		int b;
	}
	
	@Test
	public void testSum() {
		SliceCollectorTestdata sampleProgram = new SliceCollectorTestdata();
		sampleProgram.setA(3);
		int sum = sampleProgram.getSum(2, 5);
		System.out.println("run test 2");
		assertEquals(sum, 7);
	}
}
