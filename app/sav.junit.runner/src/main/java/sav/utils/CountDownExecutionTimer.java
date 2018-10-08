/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	static int i = 0;
	
	static {
		cleanUpTimer = new Timer();
		cleanUpTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				long curTime = System.currentTimeMillis();
				List<Thread> queueThreads = new ArrayList<Thread>();
				synchronized (abandonedThreads) {
					for (Iterator<Thread> it = abandonedThreads.keySet().iterator(); it.hasNext();) {
						Thread thread = it.next();
						long timeToStop = abandonedThreads.get(thread);
						if (timeToStop < curTime) {
							queueThreads.add(thread);
							it.remove();
						}
					}
				}
//					System.out.println(String.format("CleanUpThread: %s at %s (%s threads)", i++, TimeUtils.getCurrentTimeStamp(), abandonedThreads.size()));
//					StringBuffer sb = new StringBuffer("Threads: ");
//					for (Thread thread : abandonedThreads.keySet()) {
//						sb.append(thread.getId()).append(", ");
//					}
//					System.out.println(sb.toString());
				for (Thread thread : queueThreads) {
					try {
						if (thread != null && thread.isAlive()) {
							long threadId = thread.getId();
							System.out.println(String.format("stop thread %s (%s)", threadId, thread.getName()));
							try {
								thread.stop();
							} catch(Throwable ex) {
								ex.printStackTrace(System.out);
							}
							System.out.println(String.format("thread %s stopped!", threadId));
						}
					} catch (Throwable t) {
						// ignore
					}
				}
			}
			
		}, 100l, 100l);
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
					target.onTimeout();
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
				}
			}
		}, timeout);
		thread.start();
		try {
			executor.latch.await();
		} catch (InterruptedException e) {
			// do nothing
		}
		if (executor.isSuccess) {
			timer.cancel();
		}
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
	public void refresh() {
		
	}
}
