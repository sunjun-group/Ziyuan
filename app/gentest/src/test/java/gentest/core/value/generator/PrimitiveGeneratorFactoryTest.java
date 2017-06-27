/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import org.junit.Test;

import gentest.core.value.generator.EnumContainer.RoundingMode;
import gentest.core.value.generator.PrimitiveGeneratorFactory.PrimitiveGenerator;

/**
 * @author LLT
 *
 */
public class PrimitiveGeneratorFactoryTest {

	@Test
	public void getEnumGenerator() {
		PrimitiveGeneratorFactory factory = new PrimitiveGeneratorFactory();
		Class<?> type = RoundingMode.ROUND_CEIL.getClass();
		PrimitiveGenerator<?> generator = factory.getGenerator(type);
		System.out.println(generator.next(type));
	}
}
