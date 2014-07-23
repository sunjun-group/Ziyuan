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
public class FindMaxCallerFailTest1 {

	@Test
	public void test2() {
		List<Integer> arr = intList(10, 60, 30, 50, 100);
		int max = FindMax.findMax(arr);
		Assert.assertEquals(max, 100);
	}

	protected List<Integer> intList(Integer... values) {
		return Arrays.asList(values);
	}

}
