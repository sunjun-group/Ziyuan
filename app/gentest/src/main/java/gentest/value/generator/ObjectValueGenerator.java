/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import gentest.data.statement.RConstructor;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class ObjectValueGenerator extends AbstractValueGenerator {

	@Override
	public void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		Constructor<?> constructor = findConstructor(type);
		RConstructor rconstructor = RConstructor.of(constructor);
		Type[] types = constructor.getGenericParameterTypes();
		int[] paramIds = new int[types.length];
		for (int i = 0; i < rconstructor.getInputTypes().size(); i++) {
			Class<?> paramType = rconstructor.getInputTypes().get(i);
			GeneratedVariable newVariable = variable.newVariable();
			append(newVariable, level + 1, paramType, types[i]);
			variable.append(newVariable);
			paramIds[i] = newVariable.getReturnVarId();
		}
		variable.append(rconstructor, paramIds);
	}

	private Constructor<?> findConstructor(Class<?> type) {
		return type.getConstructors()[0];
	}

}
