/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import gentest.data.statement.Rmethod;
import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class MapValueGenerator extends SpecificValueGenerator {
	private Type type;
	
	public MapValueGenerator(Type type) {
		super(HashMap.class, null);
		this.type = type;
	}
	
	@Override
	protected void doAppendMethod(GeneratedVariable variable, int level,
			int scopeId) throws SavException {
		Method method = getPutMethod();
		int elementNum = Randomness.nextRandomInt(10);
		Pair<Class<?>, Type> keyType = getContentType(type, 0);
		Pair<Class<?>, Type> valueType = getContentType(type, 1);
		for (int eleI = 0; eleI < elementNum; eleI++) {
			GeneratedVariable keyVariable = variable.newVariable();
			ValueGenerator.append(keyVariable, level + 1, keyType.a,
					keyType.b);
			variable.append(keyVariable);
			GeneratedVariable valueVariable = variable.newVariable();
			ValueGenerator.append(valueVariable, level + 1, valueType.a,
					valueType.b);
			variable.append(valueVariable);
			Rmethod rmethod = new Rmethod(method, scopeId);
			variable.append(rmethod, new int[] { keyVariable.getReturnVarId(),
					valueVariable.getReturnVarId() }, false);
		}
	}

	private Method getPutMethod() {
		try {
			return Map.class.getMethod("put", Object.class, Object.class);
		} catch (SecurityException e) {
			throw new RuntimeException("cannot find method Map.put", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("cannot find method Map.put", e);
		}
	}

	public static boolean accept(Class<?> type) {
		return type.isAssignableFrom(Map.class);
	}
}
