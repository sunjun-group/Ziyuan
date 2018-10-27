/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author LLT
 *
 */
public abstract class ExecutionTimer {
	private long defaultTimeout;
	
	protected ExecutionTimer(long timeout) {
		this.defaultTimeout = timeout;
	}
	
	protected ExecutionTimer(int timeout, TimeUnit unit) {
		this.defaultTimeout = unit.toMillis(timeout);
	}
	
	/**
	 * @return whether the process is success or not.
	 */
	public boolean run(final Runnable target) {
		return run(target, defaultTimeout);
	}
	
	public abstract boolean run(final Runnable target, long timeout);
	
	
	public static ExecutionTimer getDefaultExecutionTimer(long defaultTimeout) {
		return getCountDownExecutionTimer(defaultTimeout);
	}

	public static ExecutionTimer getDefaultExecutionTimer(int defaultTimeout, TimeUnit timeunit) {
		return new CountDownExecutionTimer(defaultTimeout, timeunit);
	}
	
	@Deprecated
	public static ExecutionTimer getExecutionTimer(int defaultTimeout, TimeUnit timeunit) {
		return new ThreadKillExecutionTimer(defaultTimeout, timeunit);
	}
	
	public static ExecutionTimer getFutureTaskExecutionTimer(long defaultTimeout) {
		return new FutureTaskExecutionTimer((int) defaultTimeout, TimeUnit.MILLISECONDS);
	}
	
	public static ExecutionTimer getCachePoolExecutionTimer(long defaultTimeout) {
		return new CachePoolExecutionTimer(defaultTimeout);
	}
	
	public static ExecutionTimer getCountDownExecutionTimer(long defaultTimeout) {
		return new CountDownExecutionTimer(defaultTimeout);
	}
	
	public void shutdown() {
		// do nothing by default
	}
}
