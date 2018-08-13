/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	protected CachePoolExecutionTimer(long defaultTimeout) {
		super(defaultTimeout);
	}

	@Override
	public boolean run(Runnable target, long timeout) {
		refreshExecutorService();
		executorService.execute(target);
		try {
			executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	private void refreshExecutorService() {
		if (executorService == null) {
			executorService = new CustomizedThreadPoolExecutor();
		}
	}
	
	private class CustomizedThreadPoolExecutor extends ThreadPoolExecutor {
		private List<Thread> cachedRunningThreads = new ArrayList<>();
		private Map<Runnable, Thread> runnableThreadMap = new HashMap<>();
		
		CustomizedThreadPoolExecutor() {
			this (Executors.defaultThreadFactory());
		}
		
		CustomizedThreadPoolExecutor(ThreadFactory threadFactory) {
			super(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			cachedRunningThreads.add(t);
			runnableThreadMap.put(r, t);
			super.beforeExecute(t, r);
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			Thread correspondingThread = runnableThreadMap.remove(r);
			cachedRunningThreads.remove(correspondingThread);
			super.afterExecute(r, t);
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public List<Runnable> shutdownNow() {
			List<Runnable> runnables = super.shutdownNow();
			int killedThreads = 0;
			for (Thread runningThread : cachedRunningThreads) {
				if (runningThread.isAlive()) {
					runningThread.stop();
					killedThreads++;
				}
			}
			if (killedThreads > 0) {
				log.debug(String.format("CachePoolExecutionTimer: kill %d threads due to unable to stop by interrupting!", killedThreads));
			}
			cachedRunningThreads.clear();
			runnableThreadMap.clear();
			return runnables;
		}
	}
	
	public boolean hasUnStoppableTask() {
		return !executorService.cachedRunningThreads.isEmpty();
	}

	@Override
	public void shutdown() {
		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
	}
	
}
