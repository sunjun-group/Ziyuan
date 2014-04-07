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


/**
 * @author LLT
 *
 */
public interface ILogger<T extends ILogger<T>> {
	public T info(Object... msgs);
	
	public T error(Object... msgs);
	
	public void logEx(Exception ex, String msg);
	
	public void logEx(TzException ex);
	
	public void logEx(TzRuntimeException ex);
	
	public void close();

	public void debug(String msg);
	
	public void debug(Object... msgs);

	public void debug(QueryTrace queryTrace, int queryIndex, int varIndex);
}
