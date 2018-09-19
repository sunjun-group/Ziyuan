/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LLT
 *
 */
public class CachePoolExecutionTimer extends ExecutionTimer {
	private static Logger log = LoggerFactory.getLogger(CachePoolExecutionTimer.class);
	private CustomizedThreadPoolExecutor executorService;
	private Timer cleanUpTimer;
	
	protected CachePoolExecutionTimer(long defaultTimeout) {
		super(defaultTimeout);
		cleanUpTimer = new Timer();
		cleanUpTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				threadFactory.checkTerminatedThreads();
				long curTime = System.currentTimeMillis();
				synchronized (abandonedThreads) {
					for (Iterator<Thread> it = abandonedThreads.keySet().iterator(); it.hasNext();) {
						try {
							Thread thread = it.next();
							if (thread != null && thread.isAlive()) {
								if (abandonedThreads.get(thread) < curTime) {
									log.debug("stop thread " + thread.getId());
									thread.stop();
									it.remove();
									log.debug("after stopping thread..");
								}
							} else {
								it.remove();
							}
						} catch (Throwable t) {
							// ignore
							log.debug("after stopping thread..");
						}
					}
				}
			}
		}, 5000l, 5000l);
	}
	
	@Override
	public boolean run(Runnable target, long timeout) {
		if (executorService == null) {
			executorService = new CustomizedThreadPoolExecutor();
		}
		executorService.execute(target);
		try {
			executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
	
	private volatile Map<Thread, Long> abandonedThreads = new HashMap<>();
	private CustomizedThreadFactory threadFactory = new CustomizedThreadFactory();
	private class CustomizedThreadPoolExecutor extends ThreadPoolExecutor {
		
		CustomizedThreadPoolExecutor() {
			super(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    threadFactory);
		}

		@Override
		public List<Runnable> shutdownNow() {
			List<Runnable> runnables = super.shutdownNow();
			long timeout = System.currentTimeMillis() + 1000l;
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
			return runnables;
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
	
	public boolean cleanUpThreads() { 
		threadFactory.checkTerminatedThreads();
		if (executorService != null && !threadFactory.createdThreads.isEmpty()) {
			shutdown();
			return true;
		}
		return false;
	}

	@Override
	public void shutdown() {
		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
	}
	
}
