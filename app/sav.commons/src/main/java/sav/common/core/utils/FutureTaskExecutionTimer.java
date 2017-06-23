/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author LLT
 *
 */
public class FutureTaskExecutionTimer extends ExecutionTimer {

	protected FutureTaskExecutionTimer(int timeout, TimeUnit unit) {
		super(timeout, unit);
	}

	@Override
	public boolean run(Runnable target) {
		FutureTask<?> theTask = null;
		try {
			theTask = new FutureTask<Object>(target, null);

			Thread t = new Thread(theTask);
			t.start();

			/**
			 * according to jdk document, the get methods will block if the
			 * computation has not yet completed
			 */
			theTask.get(2L, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			return false;
		} catch (InterruptedException e) {
			// do nothing
		} catch (ExecutionException e) {
			return false;
		}
		return true;
	}

}
