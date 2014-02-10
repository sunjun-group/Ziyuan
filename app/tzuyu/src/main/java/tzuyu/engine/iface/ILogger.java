/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;


/**
 * @author LLT
 *
 */
public interface ILogger<T extends ILogger<T>> {
	public T info(Object... msgs);
	
	public T error(Object... msgs);
	
	public void close();
}
