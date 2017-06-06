/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author LLT
 *
 */
public class CountDownExecutionTimer extends ExecutionTimer {
	/* allow the timertask to wait another 2 second before stop the running thread */
	private static final long CONCESSION_TIME = 2000l; 
	protected CountDownExecutionTimer(long timeout) {
		super(timeout);
	}

	protected CountDownExecutionTimer(int timeout, TimeUnit unit) {
		super(timeout, unit);
	}

	public boolean run(final Runnable target) {
		final Executor executor = new Executor(target);
		final Thread thread = new Thread(executor);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (thread.isAlive()) {
					/*
					 * by calling interrupt(), thread with
					 * infinitive loop will still be running forever.
					 */
					thread.interrupt();
					executor.isTimeout = true;
					executor.latch.countDown();
					/* allow the thread to run for a while, if after concession time,
					 * it is still alive, we have to force to kill it */
					try {
						Thread.sleep(CONCESSION_TIME);
					} catch (InterruptedException e) {
						// do nothing
					}
					if (thread.isAlive()) {
						thread.stop();
					}
				}
			}
		}, timeout);
		thread.start();
		try {
			executor.latch.await();
		} catch (InterruptedException e) {
			// do nothing
		}
		timer.cancel();
		return executor.isTimeout;
	}
	
	protected static class Executor implements Runnable {
		boolean isTimeout = false;
		Runnable runnable;
		CountDownLatch latch = new CountDownLatch(1);

		Executor(Runnable runnable) {
			this.runnable = runnable;
		}
		
		public void run() {
			try {
				runnable.run();
			} finally {
				latch.countDown();
			}
		}
	}
}
