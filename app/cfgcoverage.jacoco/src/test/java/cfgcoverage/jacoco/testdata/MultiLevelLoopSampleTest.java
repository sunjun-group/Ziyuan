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
public class MultiLevelLoopSampleTest {

	@Test
	public void test1() {
		MultiLevelLoopSample sample = new MultiLevelLoopSample();
		sample.run(0, 0, 1, 2);
	}
	
	@Test
	public void test2() {
		MultiLevelLoopSample sample = new MultiLevelLoopSample();
		sample.run(0, 2, 1, 2);
	}
	
	@Test
	public void test3() {
		MultiLevelLoopSample sample = new MultiLevelLoopSample();
		sample.run(4, 5, 3, 2);
	}
}
