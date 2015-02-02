/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store.iface;

import gentest.core.data.variable.GeneratedVariable;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author LLT
 * 
 */
public interface IVariableStore {
	
	public void put(Type type, Class<?> clazz, GeneratedVariable variable);
	
	public List<GeneratedVariable> getVariableByType(Type type, Class<?> clazz);
}
