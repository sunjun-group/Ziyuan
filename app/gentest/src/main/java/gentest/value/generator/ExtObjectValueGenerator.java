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
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.GentestConstants;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 *
 */
public class ExtObjectValueGenerator extends ObjectValueGenerator {
	private Class<?> implClazz;
	private Type type;
	private List<Method> methodcalls;
	
	public ExtObjectValueGenerator(Class<?> implClazz, Type type,
			List<String> methodSigns) {
		this.implClazz = implClazz;
		initMethodCalls(methodSigns);
		this.type = type;
	}
	
	public ExtObjectValueGenerator(List<String> methodSigns, Class<?> implClazz) {
		this.implClazz = implClazz;
		methodcalls = MethodUtils.findMethods(implClazz, methodSigns);
	}
	
	protected ExtObjectValueGenerator(Class<?> implClazz, List<Method> methods) {
		this.implClazz = implClazz;
		this.methodcalls = methods;
	}

	private void initMethodCalls(List<String> methodSigns) {
		methodcalls = new ArrayList<Method>();
		List<Method> initMethods;
		if (methodSigns == null) {
			initMethods = Arrays.asList(implClazz.getDeclaredMethods());
		} else {
			initMethods = MethodUtils.findMethods(implClazz, methodSigns);
		}
		List<Method> methodsSeq = Randomness
				.randomSequence(
						initMethods,
						GentestConstants.OBJECT_VALUE_GENERATOR_MAX_SELECTED_METHODS);
		for (Method method : methodsSeq) {
			if (MethodUtils.isPublic(method)) {
				methodcalls.add(method);
			}
		}
	}
	
	@Override
	public final boolean doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		boolean canInit = super.doAppend(variable, level, implClazz);
		int varId = variable.getLastVarId();
		variable.commitReturnVarIdIfNotExist();
		if (canInit) {
			doAppendMethod(variable, level, varId);
		}
		return canInit;
	}

	protected void doAppendMethod(GeneratedVariable variable, int level, int scopeId)
			throws SavException {
		// generate value for method call
		for (Method method : methodcalls) {
			variable.newCuttingPoint();
			doAppendMethod(variable, level, scopeId, false, method);
		}
	}
	
	@Override
	protected Pair<Class<?>, Type> getParamType(Class<?> clazz, Type type) {
		if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVar = (TypeVariable<?>) type;
			typeVar.getName();
			return getContentType((Class<?>)typeVar.getGenericDeclaration(), typeVar.getName());
		}
		return super.getParamType(clazz, type);
	}
	
	protected Pair<Class<?>, Type> getContentType(Class<?> declaredClazz, String name) {
		if (type instanceof ParameterizedType) {
			TypeVariable<?>[] typeParameters = declaredClazz.getTypeParameters();
			int paramIdx = 0;
			for (;paramIdx < typeParameters.length; paramIdx++) {
				if (name.equals(typeParameters[paramIdx].getName())) {
					break;
				}
			}
			if (paramIdx < typeParameters.length) {
				Type compType = ((ParameterizedType) type)
						.getActualTypeArguments()[paramIdx];
				if (compType instanceof Class<?>) {
					return new Pair<Class<?>, Type>((Class<?>) compType, null);
				}
				if (compType instanceof ParameterizedType) {
					return new Pair<Class<?>, Type>(
							(Class<?>) ((ParameterizedType) compType)
									.getRawType(),
							compType);
				}
			}
		}
		return new Pair<Class<?>, Type>(Object.class, null);
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
