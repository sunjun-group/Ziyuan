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
public class ThreadKillExecutionTimer extends ExecutionTimer {
	protected ThreadKillExecutionTimer(long timeout) {
		super(timeout);
	}

	protected ThreadKillExecutionTimer(int timeout, TimeUnit unit) {
		super(timeout, unit);
	}

	public boolean run(final Runnable target, long timeout) {
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
					thread.stop();
					executor.isSuccess = false;
					executor.latch.countDown();
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
		return executor.isSuccess;
	}
	
	protected static class Executor implements Runnable {
		boolean isSuccess = true;
		Runnable runnable;
		CountDownLatch latch = new CountDownLatch(1);

		Executor(Runnable runnable) {
			this.runnable = runnable;
		}
		
		public void run() {
			try {
				runnable.run();
			} catch (Throwable th) {
				isSuccess = false;
			} finally {
				latch.countDown();
			}
		}
	}
}
