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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 * 
 */
public class ListValueGenerator extends SpecificValueGenerator {
	private Type type;

	public ListValueGenerator(Class<?> clazz, Type type) {
		super(ArrayList.class, null);
		this.type = type;
	}

	public static boolean accept(Class<?> type) {
		return type == List.class;
	}

	@Override
	public void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		super.doAppend(variable, level, type);
	}

	protected void doAppendMethods(GeneratedVariable variable, int level,
			int varId) throws SavException {
		// generate value for method call
		try {
			Method method = ArrayList.class.getMethod("add", Object.class);
			int elementNum = Randomness.nextRandomInt(10);
			Pair<Class<?>, Type> paramType = getContentType();
			for (int eleI = 0; eleI < elementNum; eleI++) {
				GeneratedVariable newVariable = variable.newVariable();
				AbstractValueGenerator.append(newVariable, level + 2, paramType.a,
						paramType.b);
				Rmethod rmethod = new Rmethod(method, varId);
				variable.append(newVariable);
				variable.append(rmethod, new int[]{newVariable.getReturnVarId()});
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"cannot find method add in ArrayList class");
		}
	}

	private Pair<Class<?>, Type> getContentType() {
		if (type instanceof ParameterizedType) {
			Type compType = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (compType instanceof Class<?>) {
				return new Pair<Class<?>, Type>((Class<?>) compType, null);
			}
		}
		return new Pair<Class<?>, Type>(Object.class, null);
	}
}
