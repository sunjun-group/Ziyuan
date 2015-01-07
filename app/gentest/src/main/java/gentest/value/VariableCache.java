/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value;

import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;


/**
 * @author LLT
 *
 */
public class VariableCache {
	private Map<Type, List<GeneratedVariable>> generatedVarMap;
	private static VariableCache instance;

	public VariableCache() {
		generatedVarMap = new HashMap<Type, List<GeneratedVariable>>();
	}
	
	public void put(Class<?> clazz, GeneratedVariable variable) {
		CollectionUtils.getListInitIfEmpty(generatedVarMap, clazz)
						.add(variable);
	}
	
	
	public static VariableCache getInstance() {
		if (instance == null) {
			instance = new VariableCache();
		}
		return instance;
	}
	
}
