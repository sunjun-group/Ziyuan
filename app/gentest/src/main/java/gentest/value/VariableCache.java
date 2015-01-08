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
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 * TODO LLT: to continue
 */
public class VariableCache {
	private Map<Type, List<GeneratedVariable>> generatedVarMap;
	private static VariableCache instance;

	private VariableCache() {
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

	public GeneratedVariable select(Class<?> clazz) {
		List<GeneratedVariable> existingValue = generatedVarMap.get(clazz);
		GeneratedVariable selectedValue = Randomness.randomMember(existingValue);
		if (selectedValue != null) {
			System.out.println("class: " + clazz);
		}
		return selectedValue;
	}

	public void reset() {
		generatedVarMap.clear();
	}
	
}
