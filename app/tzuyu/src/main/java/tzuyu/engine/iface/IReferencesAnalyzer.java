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
public interface IReferencesAnalyzer {
	/**
	 * return an implementation for the interface. will be picked up randomly.
	 */
	public Class<?> getRandomImplClzz(Class<?> clazz);
}
