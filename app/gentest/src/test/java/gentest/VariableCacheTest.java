/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import gentest.builder.FixTraceGentestBuilder;
import gentest.core.value.store.VariableCache;
import gentest.variableCacheTest.ClazzWithListObjectAndListInteger;

import org.junit.Test;

import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class VariableCacheTest extends GentestForTestdataRunner {

	@Test
	public void testSelectFromCacheSameClassButDiffType() throws SavException {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(
				NUMBER_OF_TESTCASES);
		builder.forClass(ClazzWithListObjectAndListInteger.class)
				.method("func1")
				.method("func2");
		printTc(builder, ClazzWithListObjectAndListInteger.class);
	}
	
	@Test
	public void testCache() throws Throwable {
		Class<?> targetClazz = VariableCache.class;
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(
				NUMBER_OF_TESTCASES);
		builder.forClass(targetClazz)
				.method("put")
				.method("select");
		printTc(builder, targetClazz);
	}
}
