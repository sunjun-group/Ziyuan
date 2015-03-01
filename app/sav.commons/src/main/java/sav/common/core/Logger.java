/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public abstract class Logger<T extends Logger<T>> {
	/* TODO - nice to have: better manage logger if having time */
	private static Logger<?> defaultLogger;
	public static boolean debug;
	
	public static Logger<?> getDefaultLogger() {
		if (defaultLogger == null) {
			return CommandLineLogger.instance();
		}
		return defaultLogger;
	}
	
	public static void setDefaultLogger(Logger<?> defaultLogger) {
		Logger.defaultLogger = defaultLogger;
	}
	
	public abstract T info(Object... msgs);

	public abstract T error(Object... msgs);
	
	public abstract T warn(Object... msgs);

	public abstract void logEx(Exception ex, String msg);

	public abstract T debug(String msg);

	public void logEx(SavException ex) {
		logEx(ex, ex.getType());
	}

	public void logEx(SavRtException ex) {
		logEx(ex, ex.getType());
	}

	protected abstract void logEx(Exception ex, Enum<?> type);

	@SuppressWarnings("unchecked")
	public T debug(Object... msgs) {
		if (isDebug()) {
			String msg = StringUtils.spaceJoin(msgs);
			debug(msg);
		}
		return (T) this;
	}

	public boolean isDebug() {
		return debug;
	}
}
