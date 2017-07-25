/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

/**
 * @author LLT
 *
 */
public interface ISubTypesScanner {
	/**
	 * return an implementation for the interface. will be picked up randomly.
	 */
	public Class<?> getRandomImplClzz(Class<?> clazz);

	public Class<?> getRandomImplClzz(Class<?>[] bounds);

	public Class<?> getRandomImplClzz(IType itype);

}
