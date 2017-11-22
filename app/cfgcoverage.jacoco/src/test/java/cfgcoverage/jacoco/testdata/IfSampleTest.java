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
	public void test0() {
		IfSample sample = new IfSample();
		sample.multiCondOr(1, 3);
	}
	
	@Test
	public void test3() {
		IfSample sample = new IfSample();
		sample.multiCondOr(2, 2);
	}
	
	@Test
	public void test4() {
		IfSample sample = new IfSample();
		sample.multiCondOr(4, -1);
	}
	
	@Test
	public void test1() {
		IfSample sample = new IfSample();
		sample.multiCondAnd(3, 1);
	}
	
	@Test
	public void test5() {
		IfSample sample = new IfSample();
		sample.multiCondAnd(1, 1);
	}
	
	@Test
	public void test6() {
		IfSample sample = new IfSample();
		sample.multiCondAnd(2, -1);
	}
	
	@Test
	public void test7() {
		IfSample sample = new IfSample();
		sample.multiCondAnd(2, 6);
	}
	
	@Test
	public void test2() {
		IfSample sample = new IfSample();
		sample.multiCondAndOr(1, -1);
	}
	
	@Test
	public void test8() {
		IfSample sample = new IfSample();
		sample.multiCondAndOr(2, 1);
	}
	
	@Test
	public void test9() {
		IfSample sample = new IfSample();
		sample.multiCondAndOr(4, 10);
	}
	

	
}
