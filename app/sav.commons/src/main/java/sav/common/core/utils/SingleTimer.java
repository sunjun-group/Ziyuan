/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

/**
 * @author LLT
 *
 */
public class SingleTimer {
	private long start;
	private String taskName;
	private long exectionTime = -1;
	private long timeLimit;

	public SingleTimer(String taskName, long start) {
		this.taskName = taskName;
		this.start = start;
	}

	public static SingleTimer start(String taskName) {
		SingleTimer timer = new SingleTimer(taskName, currentTime());
		return timer;
	}
	
	public void restart() {
		start = currentTime();
	}

	private static long currentTime() {
		return System.currentTimeMillis();
	}

	public long getExecutionTime() {
		if (exectionTime > 0) {
			return exectionTime;
		}
		return currentTime() - start;
	}
	
	public void captureExecutionTime() {
		exectionTime = getExecutionTime();
	}
	
	public void clearExecutionTime() {
		exectionTime = -1;
	}
	
	public boolean isTimeout() {
		return getExecutionTime() > timeLimit;
	}
	
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void logResults(org.slf4j.Logger log) {
		if (!log.isDebugEnabled()) {
			return;
		}
		log.debug("{}: {}", taskName, TextFormatUtils.printTimeString(getExecutionTime()));
	}
	
	public boolean logResults(org.slf4j.Logger log, long maxRt) {
		if (getExecutionTime() > maxRt) {
			logResults(log);
			return true;
		}
		return false;
	}
	
	public String getResult() {
		return String.format("%s: %s", taskName, TextFormatUtils.printTimeString(getExecutionTime()));
	}
}
