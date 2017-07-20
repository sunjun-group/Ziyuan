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
public class IfInLoopTest {
	
	@Test
	public void test() {
		IfInLoop sample = new IfInLoop();
		sample.run(2);
		sample.run(-1);
	}
}
