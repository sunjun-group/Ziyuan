/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.iface;

import sav.common.core.SavException;
import sav.common.core.SavRtException;

/**
 * @author LLT
 *
 */
public interface ILogger<T extends ILogger<T>> {
	public T info(Object... msgs);
	
	public T error(Object... msgs);
	
	public void logEx(Exception ex, String msg);
	
	public void logEx(SavException ex);
	
	public void logEx(SavRtException ex);
	
	public void close();

	public void debug(String msg);
	
	public void debug(Object... msgs);
}
