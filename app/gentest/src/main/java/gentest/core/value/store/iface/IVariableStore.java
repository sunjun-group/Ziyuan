/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store.iface;

import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;

import java.util.List;

/**
 * @author LLT
 * 
 */
public interface IVariableStore {
	
	public void put(IType type, GeneratedVariable variable);
	
	public List<GeneratedVariable> getVariableByType(IType type);
}
