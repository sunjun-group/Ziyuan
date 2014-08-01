/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import sav.common.core.iface.ILogger;
import tzuyu.engine.model.QueryTrace;

/**
 * @author LLT
 *
 */
public abstract class TzAbstractLogger<T extends TzAbstractLogger<T>> extends
		sav.common.core.AbstractLogger<T> implements ILogger<T> {

	public void debug(QueryTrace queryTrace, int queryIndex, int varIndex) {
		if (isDebug()) {
			info("QueryTrace.getVariableForStatement(): query="
					+ queryTrace.getQuery().toString() + 
					"\nsequence: " + queryTrace.getSequence().toString()
					+ "\nqueryIndex=" + queryIndex + " , varIdx=" + varIndex);
		}
	}
	
}
