/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.store.iface.IVariableStore;
import gentest.injection.TestcaseGenerationScope;
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
@TestcaseGenerationScope
public class VariableCache implements IVariableStore {
	private Logger<?> log = Logger.getDefaultLogger();
	private Map<Type, List<GeneratedVariable>> generatedVarMap;

	public VariableCache() {
		generatedVarMap = new HashMap<Type, List<GeneratedVariable>>();
	}

	public void put(IType type, GeneratedVariable variable) {
		List<GeneratedVariable> valueList;
		if (type.getType() != null) {
			valueList = CollectionUtils.getListInitIfEmpty(generatedVarMap, type.getType());
		} else {
			valueList = CollectionUtils.getListInitIfEmpty(generatedVarMap, type.getRawType());
		}
		if (valueList.size() == GentestConstants.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE) {
//			log.debug("VariableCache.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE reach (class: ",
//					type.getRawType().getName(), ", type: ", type);
			int randomPos = Randomness.nextInt(valueList.size());
			valueList.set(randomPos, variable);
		} else {
			valueList.add(variable);
		}
	}

	public List<GeneratedVariable> getVariableByType(IType type) {
		List<GeneratedVariable> result = null;
		if (type.getType() != null) {
			result = generatedVarMap.get(type.getType());
		} else {
			result = generatedVarMap.get(type.getRawType());
		}
		if (result == null) {
			return new ArrayList<GeneratedVariable>(0);
		}
		return result;
	}
	
}
