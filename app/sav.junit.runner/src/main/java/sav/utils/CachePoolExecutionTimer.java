/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LLT
 *
 */
public class CachePoolExecutionTimer implements IExecutionTimer {
	private volatile CustomizedThreadPoolExecutor executorService;
	private volatile Map<Thread, Long> abandonedThreads = new HashMap<>();
	private CustomizedThreadFactory threadFactory = new CustomizedThreadFactory();
	private Timer cleanUpTimer;
	
	protected CachePoolExecutionTimer() {
		cleanUpTimer = new Timer();
		cleanUpTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				refresh();
				long curTime = System.currentTimeMillis();
				synchronized (abandonedThreads) {
//					System.out.println(TimeUtils.getCurrentTimeStamp());
//					StringBuffer sb = new StringBuffer("Cleanup-Threads: ");
//					for (Thread thread : abandonedThreads.keySet()) {
//						sb.append(thread.getId()).append(", ");
//					}
//					System.out.println(sb.toString());
					for (Iterator<Thread> it = abandonedThreads.keySet().iterator(); it.hasNext();) {
						try {
							Thread thread = it.next();
							long timeToStop = abandonedThreads.get(thread);
							if (thread != null && thread.isAlive()) {
//								System.out.println(String.format("Thread %s, time to stop: %s, curTime: %s",
//										thread.getId(), timeToStop, curTime));
								if (timeToStop < curTime) {
									long threadId = thread.getId();
									System.out.println(String.format("stop thread %s (%s)", threadId, thread.getName()));
									try {
										thread.stop();
									} catch(Throwable ex) {
										ex.printStackTrace(System.out);
									}
//									System.out.println(String.format("agent - thread %s stopped!", threadId));
									it.remove();
								}
							} else {
								it.remove();
							}
						} catch (Throwable t) {
							// ignore
							t.printStackTrace(System.out);
						}
					}
				}
			}

		}, 500l, 50l);
	}
	
	@Override
	public boolean run(TestRunner target, long timeout) {
		if (executorService == null) {
			executorService = new CustomizedThreadPoolExecutor();
		}
		executorService.execute(target);
		try {
			executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			target.onTimeout();
			return false;
		}
		return true;
	}
	
	private class CustomizedThreadPoolExecutor extends ThreadPoolExecutor {
		
		CustomizedThreadPoolExecutor() {
			super(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    threadFactory);
		}

		public void cleanup() {
			long timeout = System.currentTimeMillis() + 50l;
			synchronized (abandonedThreads) {
				synchronized (threadFactory.createdThreads) {
					for (Thread runningThread : threadFactory.createdThreads) {
						if (runningThread != null && runningThread.isAlive()) {
							abandonedThreads.put(runningThread, timeout);
						}
					}
					threadFactory.createdThreads.clear();
				}
			}
		}
	}
	
	private static class CustomizedThreadFactory implements ThreadFactory {
		private ThreadFactory defautThreadFactory = Executors.defaultThreadFactory();
		private volatile Set<Thread> createdThreads = new HashSet<>();
		
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = defautThreadFactory.newThread(r);
			createdThreads.add(thread);
			return thread;
		}
		
		public void checkTerminatedThreads() {
			synchronized (createdThreads) {
				for (Iterator<Thread> it = createdThreads.iterator(); it.hasNext();) {
					Thread t = it.next();
					if (!t.isAlive()) {
						it.remove();
					}
				}
			}
		}
	}
	
	@Override
	public void refresh() {
		threadFactory.checkTerminatedThreads();
		if (executorService != null && !threadFactory.createdThreads.isEmpty()) {
			executorService.cleanup();
		}
	}

	
}
