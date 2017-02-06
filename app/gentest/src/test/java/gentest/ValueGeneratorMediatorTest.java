/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;


import gentest.core.value.generator.ValueGeneratorMediator;
import gentest.main.GentestConstants;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author LLT
 *
 */
public class ValueGeneratorMediatorTest {

	@Test
	public void calculateProbToGetValFromCache() {
		ValueGeneratorMediatorMock mediator = new ValueGeneratorMediatorMock();
		Assert.assertEquals(0.5, mediator.calculateProbToGetValFromCache(0), 0);
		Assert.assertEquals(0.9, mediator.calculateProbToGetValFromCache(GentestConstants.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE),
				0);
		Assert.assertEquals(0.85, mediator.calculateProbToGetValFromCache(35),
				0);
	}
	
	private static class ValueGeneratorMediatorMock extends ValueGeneratorMediator {
		@Override
		protected double calculateProbToGetValFromCache(int varsSizeInCache) {
			return super.calculateProbToGetValFromCache(varsSizeInCache);
		}
	}
}
