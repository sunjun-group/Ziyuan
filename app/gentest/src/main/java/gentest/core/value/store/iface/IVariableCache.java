/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store.iface;

import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.VariableCache;

import java.lang.reflect.Type;
import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author LLT
 * 
 */
@ImplementedBy(VariableCache.class)
public interface IVariableCache {
	
	public void put(Type type, Class<?> clazz, GeneratedVariable variable);
	
	public List<GeneratedVariable> getVariableByType(Type type, Class<?> clazz);
}
