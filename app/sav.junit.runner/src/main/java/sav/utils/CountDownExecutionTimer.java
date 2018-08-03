/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * @author LLT
 *
 */
public class CountDownExecutionTimer implements IExecutionTimer {
	/* allow the timertask to wait another 2 second before stop the running thread */
	private static final long CONCESSION_TIME = 2000l; 
	private static Timer timer = new Timer();

	@Override
	public boolean run(final TestRunner target, long timeout) {
		final Executor executor = new Executor(target);
		final Thread thread = new Thread(executor);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (thread.isAlive()) {
					executor.isSuccess = false;
					executor.latch.countDown();
					/*
					 * by calling interrupt(), thread with
					 * infinitive loop will still be running forever.
					 */
					thread.interrupt();
					/* allow the thread to run for a while, if after concession time,
					 * it is still alive, we have to force to kill it */
					timer.schedule(stopAliveInterruptedThread(thread), CONCESSION_TIME);
					target.onTimeout();
				}
			}
		}, timeout);
		thread.start();
		try {
			executor.latch.await();
		} catch (InterruptedException e) {
			// do nothing
		}
		return executor.isSuccess;
	}
	
	private TimerTask stopAliveInterruptedThread(final Thread thread) {
		return new TimerTask() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (thread.isInterrupted() && thread.isAlive()) {
					thread.stop();
				}
			}
		};
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
