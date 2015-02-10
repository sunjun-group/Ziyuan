/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.Type;


/**
 * @author LLT
 *
 */
public interface IType {
	//TODO LLT[gentest clean up]: to remove this method
	public Type getType();
	
	public Class<?> getRawType();

	public IType resolveType(Class<?> a);
	
	public IType resolveType(Type type);

	public IType[] resolveType(Type[] type);

	public IType resolveSubType(Class<?> a); 
	
	public boolean isArray();
	
	public IType getComponentType();
}
