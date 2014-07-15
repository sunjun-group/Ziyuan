/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

import sav.common.core.iface.ILogger;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public abstract class AbstractLogger<T extends AbstractLogger<T>> implements
		ILogger<T> {

	//@Override
	public void logEx(SavException ex) {
		logEx(ex, ex.getType());
	}

	//@Override
	public void logEx(SavRtException ex) {
		logEx(ex, ex.getType());
	}

	protected abstract void logEx(Exception ex, Enum<?> type);

	//@Override
	public void debug(Object... msgs) {
		if (isDebug()) {
			info(StringUtils.spaceJoin(msgs));
		}
	}

	protected abstract boolean isDebug();
}
