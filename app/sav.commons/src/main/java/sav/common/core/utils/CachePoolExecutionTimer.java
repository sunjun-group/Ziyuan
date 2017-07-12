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
			executorService = Executors.newCachedThreadPool();
		}
	}

	@Override
	public void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
		}
	}
	
}
