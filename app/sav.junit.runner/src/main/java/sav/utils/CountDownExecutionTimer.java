/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * @author LLT
 *
 */
public class CountDownExecutionTimer implements IExecutionTimer {
	/* allow the timertask to wait another 2 second before stop the running thread */
	private static final long CONCESSION_TIME = 50l; 
	private static Timer cleanUpTimer;
	private static volatile Map<Thread, Long> abandonedThreads = new HashMap<>();
	
	static {
		cleanUpTimer = new Timer();
		cleanUpTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				synchronized (abandonedThreads) {
					long curTime = System.currentTimeMillis();
					synchronized (abandonedThreads) {
						for (Iterator<Thread> it = abandonedThreads.keySet().iterator(); it.hasNext();) {
							try {
								Thread thread = it.next();
								long timeToStop = abandonedThreads.get(thread);
								if (thread != null && thread.isAlive()) {
									System.out.println(String.format("Thread %s, time to stop: %s, curTime: %s",
											thread.getId(), timeToStop, curTime));
									if (timeToStop < curTime) {
										long threadId = thread.getId();
										System.out.println("stop thread " + threadId);
										try {
											thread.stop();
										} catch(Exception ex) {
											ex.printStackTrace();
										}
										it.remove();
										System.out.println(String.format("thread %s stopped!", threadId));
									}
								} else {
									it.remove();
								}
							} catch (Throwable t) {
								// ignore
							}
						}
					}
				}
			}
			
		}, 1000l, 5000l);
	}
	
	@Override
	public boolean run(final TestRunner target, long timeout) {
		final Executor executor = new Executor(target);
		final Thread thread = new Thread(executor);
		Timer timer = new Timer();
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
					synchronized (abandonedThreads) {
						abandonedThreads.put(thread, System.currentTimeMillis() + CONCESSION_TIME);
					}
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

	@Override
	public void shutdown() {
		
	}
}
