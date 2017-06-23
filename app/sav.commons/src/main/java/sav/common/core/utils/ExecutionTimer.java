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
	protected long timeout;
	
	protected ExecutionTimer(long timeout) {
		this.timeout = timeout;
	}
	
	protected ExecutionTimer(int timeout, TimeUnit unit) {
		this.timeout = unit.toMillis(timeout);
	}
	
	/**
	 * @return whether the process is success or not.
	 */
	public abstract boolean run(final Runnable target);
	
	public static ExecutionTimer getDefaultExecutionTimer(long timeout) {
		return new CountDownExecutionTimer(timeout);
	}
	
	public static ExecutionTimer getDefaultExecutionTimer(int timeout, TimeUnit timeunit) {
		return new CountDownExecutionTimer(timeout, timeunit);
	}
	
	@Deprecated
	public static ExecutionTimer getExecutionTimer(int timeout, TimeUnit timeunit) {
		return new OrgExecutionTimer(timeout, timeunit);
	}
	
	public static ExecutionTimer getFutureTaskExecutionTimer(long timeout) {
		return new FutureTaskExecutionTimer((int) timeout, TimeUnit.MILLISECONDS);
	}
}
