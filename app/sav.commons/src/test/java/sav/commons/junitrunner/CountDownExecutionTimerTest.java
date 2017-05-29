/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.junitrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import sav.common.core.utils.ExecutionTimer;

/**
 * @author LLT
 *
 */
public class CountDownExecutionTimerTest {

	@Test
	public void testLoop() {
		ExecutionTimer timer = ExecutionTimer.getDefaultExecutionTimer(2, TimeUnit.SECONDS);
		final List<String> result = new ArrayList<String>();
		timer.run(new Runnable() {
			
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				try {
					while (true) {
						System.out.println("running testLoop");
					}
				} finally {
					result.add("testLoop stopped");
					System.out.println("running time: " + (System.currentTimeMillis() - start));
				}
			}
		});
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
		}
		System.out.println("finish");
		Assert.assertFalse(result.isEmpty());
	}
	
}
