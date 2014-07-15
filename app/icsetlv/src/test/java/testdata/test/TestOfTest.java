/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata.test;

import org.junit.Test;

import testdata.test.Test.NewType;

/**
 * @author LLT
 *
 */
public class TestOfTest {
	
	@Test
	public void runTest() {
		testdata.test.Test test1 = new testdata.test.Test(0, 0);
		test1.getType().anotherType = new NewType();
		test1.execute();
	}
	
	@Test
	public void testFail() {
		testdata.test.Test test1 = new testdata.test.Test(90, 20);
		test1.getType().anotherType = new NewType();
		test1.execute();
	}
	
	@Test
	public void testFail2() {
		testdata.test.Test test1 = new testdata.test.Test(100, 10);
		test1.getType().anotherType = new NewType();
		test1.execute();
	}
}
