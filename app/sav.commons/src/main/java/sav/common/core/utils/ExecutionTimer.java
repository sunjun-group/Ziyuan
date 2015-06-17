/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author LLT
 *
 */
public class ExecutionTimer {
	private long timeout;
	
	public ExecutionTimer(long timeout) {
		this.timeout = timeout;
	}
	
	public ExecutionTimer(int timeout, TimeUnit unit) {
		this.timeout = unit.toMillis(timeout);
	}
	
	public void run(final Runnable target) {
		final Thread thread = new Thread(target);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (thread.isAlive()) {
					/* we must stop, cannot call interrupt,
					 * otherwise, infinitive loop will still be running 
					 */
					thread.stop();
				}
			}
		}, timeout);
		thread.start();
		synchronized (thread) {
			try {
				/* make the current thread wait until target thread stops */
				thread.wait(); 
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		timer.cancel();
	}
}
