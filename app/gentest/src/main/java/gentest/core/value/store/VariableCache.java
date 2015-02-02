/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.store.iface.IVariableStore;
import gentest.main.GentestConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import sav.common.core.Logger;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 */
public class VariableCache implements IVariableStore {
	private Logger<?> log = Logger.getDefaultLogger();
	private Map<Type, List<GeneratedVariable>> generatedVarMap;

	public VariableCache() {
		generatedVarMap = new HashMap<Type, List<GeneratedVariable>>();
	}

	public void put(Type type, Class<?> clazz, GeneratedVariable variable) {
		List<GeneratedVariable> valueList;
		if (type != null) {
			valueList = CollectionUtils.getListInitIfEmpty(generatedVarMap, type);
		} else {
			valueList = CollectionUtils.getListInitIfEmpty(generatedVarMap, clazz);
		}
		if (valueList.size() == GentestConstants.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE) {
			log.debug("VariableCache.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE reach (class: ",
					clazz.getName(), ", type: ", type);
			int randomPos = Randomness.nextRandomInt(valueList.size());
			valueList.set(randomPos, variable);
		} else {
			valueList.add(variable);
		}
	}

	public GeneratedVariable select(Type type, Class<?> clazz) {
		List<GeneratedVariable> existingValue = getVariableByType(type, clazz);
		GeneratedVariable selectedValue = Randomness.randomMember(existingValue);
		if (selectedValue != null) {
			System.out.println("class: " + clazz);
		}
		return selectedValue;
	}

	public List<GeneratedVariable> getVariableByType(Type type, Class<?> clazz) {
		List<GeneratedVariable> result = null;
		if (type != null) {
			result = generatedVarMap.get(type);
		} else {
			result = generatedVarMap.get(clazz);
		}
		if (result == null) {
			return new ArrayList<GeneratedVariable>(0);
		}
		return result;
	}

	public void clear() {
		generatedVarMap.clear();
	}
	
}
