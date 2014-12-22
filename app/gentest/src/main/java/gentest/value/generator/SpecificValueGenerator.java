/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import gentest.commons.utils.MethodUtils;
import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;


/**
 * @author LLT
 *
 */
public abstract class SpecificValueGenerator extends ValueGenerator {
	private Class<?> implType;
	private List<Method> methodcalls;
	
	public SpecificValueGenerator(List<String> methodSigns, Class<?> implType) {
		this.implType = implType;
		methodcalls = MethodUtils.findMethods(implType, methodSigns);
	}
	
	public SpecificValueGenerator(Class<?> implType, List<Method> methods) {
		this.implType = implType;
		this.methodcalls = methods;
	}
	
	@Override
	public final void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		getGenerator(ObjectValueGenerator.class).doAppend(variable, level, implType);
		int varId = variable.getLastVarId();
		variable.commitReturnVarIdIfNotExist();
		doAppendMethod(variable, level, varId);
	}

	protected void doAppendMethod(GeneratedVariable variable, int level, int scopeId)
			throws SavException {
		doAppendMethods(variable, level, scopeId, CollectionUtils.nullToEmpty(methodcalls));
	}
	
	protected Pair<Class<?>, Type> getContentType(Type type, int idxType) {
		if (type instanceof ParameterizedType) {
			Type compType = ((ParameterizedType) type).getActualTypeArguments()[idxType];
			if (compType instanceof Class<?>) {
				return new Pair<Class<?>, Type>((Class<?>) compType, null);
			}
			if (compType instanceof ParameterizedType) {
				return new Pair<Class<?>, Type>(
						(Class<?>) ((ParameterizedType) compType).getRawType(),
						compType);
			}
		}
		return new Pair<Class<?>, Type>(Object.class, null);
	}

}
