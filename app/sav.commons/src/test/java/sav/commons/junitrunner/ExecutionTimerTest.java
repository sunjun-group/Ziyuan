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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;

import sav.common.core.utils.ExecutionTimer;

/**
 * @author LLT
 *
 */
public class ExecutionTimerTest {
	
	@Test
	public void testLoop() {
		ExecutionTimer timer = ExecutionTimer.getExecutionTimer(2, TimeUnit.SECONDS);
		final List<String> result = new ArrayList<String>();
		timer.run(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						System.out.println("running testLoop");
					}
				} finally {
					result.add("testLoop stopped");
				}
			}
		});
		
		System.out.println("finish");
		Assert.assertFalse(result.isEmpty());
	}
	
	@Test
	public void testNoLoop() {
		long start = System.currentTimeMillis();
		ExecutionTimer timer = ExecutionTimer.getDefaultExecutionTimer(10, TimeUnit.SECONDS);
		boolean timeout = timer.run(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("runing testNoLoop");
				throw new NullPointerException();
			}
		});
		System.out.println("finish");
		long time = System.currentTimeMillis() - start;
		Assert.assertTrue(time < 10000);
		System.out.println(time);
		System.out.println(timeout ? "timeout!" : "not timeout!");
	}
	
//	@Test
	public void testFutureTaskLoop() throws InterruptedException, ExecutionException {
		FutureTask<?> theTask = null;
		final List<String> result = new ArrayList<String>();
		try{
			theTask = new FutureTask<Object>(new Runnable(){
				@Override
				public void run() {
					try {
						while (true) {
							System.out.println("running testLoop");
						}
					} finally {
						result.add("testLoop stopped");
					}
				}
				
			}, null);
			
			Thread t = new Thread(theTask);
			t.start();
			
			/**according to jdk document, the get methods will block if the computation has not yet completed*/
			theTask.get(2L, TimeUnit.SECONDS);
		}
		catch(TimeoutException e){
			e.printStackTrace();
		}
		System.out.println("finish");
		Assert.assertFalse(result.isEmpty());
	}
}
