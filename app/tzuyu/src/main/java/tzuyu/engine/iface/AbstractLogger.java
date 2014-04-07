/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public abstract class AbstractLogger<T extends AbstractLogger<T>> implements
		ILogger<T> {

	@Override
	public void logEx(TzException ex) {
		logEx(ex, ex.getType());
	}

	@Override
	public void logEx(TzRuntimeException ex) {
		logEx(ex, ex.getType());
	}

	protected abstract void logEx(Exception ex, Enum<?> type);
	
	@Override
	public void debug(Object... msgs) {
		if (isDebug()) {
			info(StringUtils.spaceJoin(msgs));
		}
	}
	
	@Override
	public void debug(QueryTrace queryTrace, int queryIndex, int varIndex) {
		if (isDebug()) {
			info("QueryTrace.getVariableForStatement(): query="
					+ queryTrace.getQuery().toString() + 
					"\nsequence: " + queryTrace.getSequence().toString()
					+ "\nqueryIndex=" + queryIndex + " , varIdx=" + varIndex);
		}
	}

	protected abstract boolean isDebug();
}
