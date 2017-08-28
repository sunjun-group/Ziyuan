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
public class LoopHeaderSampleTest {

	@Test
	public void testMultiLoopCond() {
		LoopHeaderSample sample = new LoopHeaderSample();
		sample.multiLoopCond();;
	}
	
	@Test
	public void testMultiLoopCondNeg() {
		LoopHeaderSample sample = new LoopHeaderSample();
		sample.multiLoopCondNeg();
	}
	
	@Test
	public void testSingleLoopCond() {
		LoopHeaderSample sample = new LoopHeaderSample();
		sample.singleLoopCond();
	}
	
	@Test
	public void testForLoop() {
		LoopHeaderSample sample = new LoopHeaderSample();
		sample.forLoop();
	}
	
	@Test
	public void testDoWhileMultiCond() {
		LoopHeaderSample sample = new LoopHeaderSample();
		sample.doWhileMultiCond();
	}
	
	@Test
	public void testDoWhileSingleCondWithInLoopCond() {
		LoopHeaderSample sample = new LoopHeaderSample();
		sample.doWhileSingleCondWithInLoopCond();
	}
}
