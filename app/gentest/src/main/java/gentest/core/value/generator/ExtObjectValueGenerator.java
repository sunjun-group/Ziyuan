/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.commons.utils.MethodUtils;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.execution.VariableRuntimeExecutor;
import gentest.main.GentestConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 *
 */
public class ExtObjectValueGenerator extends ObjectValueGenerator {
	private List<Method> methodcalls;
	private Pair<Class<?>, Type>[] paramTypes;
	
	public ExtObjectValueGenerator(IType type, List<String> methodSigns) {
		super(type);
		initMethodCalls(methodSigns);
	}

	private void initMethodCalls(List<String> methodSigns) {
		methodcalls = new ArrayList<Method>();
		List<Method> initMethods;
		if (methodSigns == null) {
			initMethods = getCandidatesMethodForObjInit(type.getRawType());
		} else {
			initMethods = MethodUtils.findMethods(type.getRawType(), methodSigns);
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

	private List<Method> getCandidatesMethodForObjInit(Class<?> targetClazz) {
		Method[] declaredMethods = targetClazz.getDeclaredMethods();
		List<Method> methods = new ArrayList<Method>(declaredMethods.length);
		for (Method method : declaredMethods) {
			boolean isExcluded = false;
			for (String excludePref : GentestConstants.OBJ_INIT_EXCLUDED_METHOD_PREFIXIES) {
				if (method.getName().startsWith(excludePref)) {
					isExcluded = true;
					continue;
				}
			}
			if (!isExcluded) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	@Override
	public final boolean doAppendVariable(GeneratedVariable variable, int level)
			throws SavException {
		boolean goodVariable = super.doAppendVariable(variable, level);
		int varId = variable.getLastVarId();
		if (goodVariable) {
			variable.commitReturnVarIdIfNotExist();
			appendMethod(variable, level, varId);
		}
		return goodVariable;
	}

	protected void appendMethod(GeneratedVariable variable, int level, int scopeId)
			throws SavException {
		VariableRuntimeExecutor executor = getExecutor();
		executor.reset(variable.getFirstVarId());
		executor.start(null);
		executor.execute(variable);
		// generate value for method call
		for (Method method : methodcalls) {
			variable.newCuttingPoint();
			doAppendMethod(variable, level, scopeId, false, method);
			boolean pass = executor.execute(variable.getLastFragmentStmts());
			if (pass) {
				//TODO LLT: store to good traces, to make it systematic?
			} else {
				// undo
				variable.removeLastFragment();
				executor.reset(variable.getFirstVarId());
				executor.start(null);
				executor.execute(variable);
			}
		}
	}
	
	@Override
	protected Pair<Class<?>, Type> getParamType(Class<?> clazz, Type type) {
		if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVar = (TypeVariable<?>) type;
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
				return getParamType(paramIdx);
			}
		}
		return new Pair<Class<?>, Type>(Object.class, null);
	}
	
	@SuppressWarnings("unchecked")
	public Pair<Class<?>, Type> getParamType(int paramIdx) {
		if (paramTypes == null) {
			paramTypes = new Pair[((ParameterizedType) type).getActualTypeArguments().length];
		}
		Pair<Class<?>, Type> paramType = paramTypes[paramIdx];
		if (paramType == null) {
			Type compType = ((ParameterizedType) type)
					.getActualTypeArguments()[paramIdx];
			if (compType instanceof Class<?>) {
				paramType = new Pair<Class<?>, Type>(
						toClassItselfOrItsDelegate((Class<?>) compType),
						null);
			} else if (compType instanceof ParameterizedType) {
				paramType = new Pair<Class<?>, Type>(
						(Class<?>) ((ParameterizedType) compType)
						.getRawType(),
						compType);
			} else {
				paramType = new Pair<Class<?>, Type>(Object.class, null);
			}
			paramTypes[paramIdx] = paramType;
		}
		return paramType;
	}
	
	private Class<?> toClassItselfOrItsDelegate(Class<?> clazz) {
		if (Object.class.equals(clazz)) {
			return Randomness
					.randomMember(GentestConstants.DELEGATING_CANDIDATES_FOR_OBJECT);
		}
		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			return getSubTypesScanner().getRandomImplClzz(clazz);
		}
		return clazz;
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
