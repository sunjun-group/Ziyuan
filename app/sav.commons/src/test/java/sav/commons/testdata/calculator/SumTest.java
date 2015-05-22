/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata.calculator;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author khanh
 *
 */
public class SumTest {

	@Test
	public void whenBothZero(){
		int a = 0;
		int b = 0;
		int sum = new Sum(a, b).getSum();
		int expect = a + b;
		
		Assert.assertEquals(expect, sum);
	}
	
	@Test
	public void whenOneZero(){
		int a = 0;
		int b = 1;
		int sum = new Sum(a, b).getSum();
		int expect = a + b;
		
		Assert.assertEquals(expect, sum);
	}
	
	@Test
	public void whenTheOtherZero(){
		int a = 1;
		int b = 0;
		int sum = new Sum(a, b).getSum();
		int expect = a + b;
		
		Assert.assertEquals(expect, sum);
	}
	
	@Test
	public void whenBothSmall(){
		int a = 1;
		int b = 2;
		int sum = new Sum(a, b).getSum();
		int expect = a + b;
		
		Assert.assertEquals(expect, sum);
	}
	
	@Test
	public void whenBothBig(){
		int a = 100;
		int b = 200;
		int sum = new Sum(a, b).getSum();
		int expect = a + b;
		
		Assert.assertEquals(expect, sum);
	}
}
