/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.testdata;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class IfSampleTest {

	@Test
	public void test1() {
		IfSample sample = new IfSample();
		sample.run(1);
	}
	
	
	@Test
	public void test2() {
		IfSample sample = new IfSample();
		sample.run(-1);
	}
	
	@Test
	public void test3() {
		IfSample sample = new IfSample();
		sample.multiCond(1, 3);
	}
	
	@Test
	public void test4() {
		IfSample sample = new IfSample();
		sample.multiCond(3, 1);
	}
}
