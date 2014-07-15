/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata.slice;

import java.util.Arrays;
import java.util.List;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author LLT
 * 
 */
public class FindMaxCallerPassTest1 {

	@Test
	public void test1() {
		List<Integer> arr = intList(10, 50, 15, 30, 90, 20);
		int max = FindMax.findMax(arr);
		Assert.assertEquals(max, 90);
	}
	
	protected List<Integer> intList(Integer... values) {
		return Arrays.asList(values);
	}
}
