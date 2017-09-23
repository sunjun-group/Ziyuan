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
	
	protected CachePoolExecutionTimer(long timeout) {
		super(timeout);
		executorService = Executors.newCachedThreadPool();
	}

	@Override
	public boolean run(Runnable target) {
		executorService.execute(target);
		try {
			executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	
}
