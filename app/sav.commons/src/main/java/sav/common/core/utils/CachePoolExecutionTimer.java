/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author LLT
 *
 */
public class CachePoolExecutionTimer extends ExecutionTimer {
	private ExecutorService executorService;
	private int count;
	
	protected CachePoolExecutionTimer(long timeout) {
		super(timeout);
	}

	@Override
	public boolean run(Runnable target) {
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
		if (count == 10 || executorService == null) {
			shutdown();
			executorService = Executors.newCachedThreadPool();
			count = 0;
		}
		count++;
	}

	@Override
	public void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
		}
	}
	
}
