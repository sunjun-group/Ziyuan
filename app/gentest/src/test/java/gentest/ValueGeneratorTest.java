/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;


import org.junit.Assert;
import org.junit.Test;

import gentest.core.value.generator.ValueGenerator;
import gentest.main.GentestConstants;

/**
 * @author LLT
 *
 */
public class ValueGeneratorTest {

	@Test
	public void calculateProbToGetValFromCache() {
		Assert.assertEquals(0.5, ValueGenerator.calculateProbToGetValFromCache(0), 0);
		Assert.assertEquals(0.9, ValueGenerator.calculateProbToGetValFromCache(
									GentestConstants.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE), 0);
		Assert.assertEquals(0.85, ValueGenerator.calculateProbToGetValFromCache(35), 0);
	}
}
