/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.data.MethodCall;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.execution.VariableRuntimeExecutor;
import gentest.main.GentestConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class ObjectValueGenerator extends ValueGenerator {
	private VariableRuntimeExecutor rtExecutor = new VariableRuntimeExecutor();
	
	@Override
	public boolean doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		for (int i = 0; i < GentestConstants.OBJECT_VALUE_GENERATOR_MAX_TRY_SELECTING_CONSTRUCTOR; i++) {
			Object initializedStmt = findConstructor(type);
			if (!appendConstructor(variable, level, type, initializedStmt)) {
				// if fail, retry
				continue;
			}
			// validate the generated variable by executing it
			rtExecutor.reset(variable.getFirstVarId());
			rtExecutor.start(null);
			if (rtExecutor.execute(variable)) {
				break;
			} else {
				variable.reset();
			}
		}
		if (variable.isEmpty()) {
			/* we have to assign null to the obj */
			assignNull(variable, type);
			return false;
		}
		
		return true;
	}
	
	protected VariableRuntimeExecutor getExecutor() {
		return rtExecutor;
	}

	private boolean appendConstructor(GeneratedVariable variable, int level,
			Class<?> type, Object initializedStmt) throws SavException {
		if (initializedStmt instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) initializedStmt;
			RConstructor rconstructor = RConstructor.of(constructor);
			Type[] types = constructor.getGenericParameterTypes();
			int[] paramIds = new int[types.length];
			for (int i = 0; i < rconstructor.getInputTypes().size(); i++) {
				Class<?> paramType = rconstructor.getInputTypes().get(i);
				GeneratedVariable newVariable = appendVariable(variable, level + 1,
						paramType, types[i]);
				paramIds[i] = newVariable.getReturnVarId();
			}
			variable.append(rconstructor, paramIds);
		} else if (initializedStmt instanceof Method) {
			// init by static method
			doAppendStaticMethods(variable, level,
					CollectionUtils.listOf((Method) initializedStmt));
		} else if (initializedStmt instanceof MethodCall) {
			// init by a builder
			MethodCall methodCall = (MethodCall) initializedStmt;
			GeneratedVariable newVar = appendVariable(variable, level + 1,
					methodCall.getReceiverType(), null);
			// call the method of builder to get the object for current type.
			doAppendMethods(variable, level,
					CollectionUtils.listOf(methodCall.getMethod()),
					newVar.getReturnVarId(), true);
		} else {
			return false;
		}
		return true;
	}
	
	/**
	 * return 
	 * constructor: if the class has it own visible constructor 
	 * 			or if not, the visible constructor of extended class will be returned
	 * method: means static method, if the class does not have visible constructor but static initialization method
	 * methodCall: if the class has a builder inside. 
	 */
	private Object findConstructor(Class<?> type) {
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			// try to search subclass
			Class<?> subClass = getSubTypesScanner().getRandomImplClzz(type);
			if (subClass != null) {
				return findConstructor(subClass);
			}
		} else {
			if (Randomness
					.weighedCoinFlip(GentestConstants.PROBABILITY_OF_PUBLIC_NO_PARAM_CONSTRUCTOR)) {
				try {
					/*
					 * try with the perfect one which is public constructor with no
					 * parameter
					 */
					Constructor<?> constructor = type.getConstructor();
					if (canBeCandidateForConstructor(constructor, type)) {
						return constructor;
					}
				} catch (Exception e) {
					// do nothing, just keep trying.
				}
			}
			List<Constructor<?>> candidates = new ArrayList<Constructor<?>>();
			for (Constructor<?> constructor : type.getConstructors()) {
				if (canBeCandidateForConstructor(constructor, type)) {
					candidates.add(constructor);
				}
			}
			if (!candidates.isEmpty()) {
				return Randomness.randomMember(candidates);
			}
		}
		
		/* try to find static method for initialization inside class */
		for (Method method : type.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())) {
				if (method.getReturnType().equals(type)) {
					return method;
				}
			}
		}
		
		/* try to find a builder inside class */
		Class<?>[] declaredClasses = type.getDeclaredClasses();
		if (declaredClasses != null) {
			for (Class<?> innerClazz : declaredClasses) {
				for (Method method : innerClazz.getMethods()) {
					if (method.getReturnType().equals(type)) {
						return MethodCall.of(method, innerClazz);
					}
				}
			}
		}

		// if still cannot get constructor,
		// try to search subclass if it's not an abstract class
		if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
			Class<?> subClass = getSubTypesScanner().getRandomImplClzz(type);
			if (subClass != null) {
				return findConstructor(subClass);
			}
		}
		
		// cannot find constructor;
		return null;
	}

	private boolean canBeCandidateForConstructor(Constructor<?> constructor, Class<?> type) {
		for (Class<?> paramType : constructor.getParameterTypes()) {
			if (type.equals(paramType)) {
				return false;
			}
		}
		return Modifier.isPublic(constructor.getModifiers());
	}

	protected final void doAppendStaticMethods(GeneratedVariable variable, int level,
			List<Method> methodcalls) throws SavException {
		doAppendMethods(variable, level, methodcalls, Statement.INVALID_VAR_ID,
				true);
	}
	
	protected void doAppendMethods(GeneratedVariable variable, int level, 
			List<Method> methodcalls, int scopeId, boolean addVariable) throws SavException {
		// generate value for method call
		for (Method method : methodcalls) {
			doAppendMethod(variable, level, scopeId, addVariable, method);
		}
	}

	protected void doAppendMethod(GeneratedVariable variable, int level,
			int scopeId, boolean addVariable, Method method)
			throws SavException {
		/* check generic types */
		Type[] genericParamTypes = method.getGenericParameterTypes();
		Class<?>[] types = method.getParameterTypes();
		int[] paramIds = new int[genericParamTypes.length];
		for (int i = 0; i < paramIds.length; i++) {
			Type type = genericParamTypes[i];
			Pair<Class<?>, Type> paramType = getParamType(types[i], type);
			GeneratedVariable newVar = appendVariable(variable,
					level + 2, paramType.a, paramType.b);
			paramIds[i] = newVar.getReturnVarId();
		}
		Rmethod rmethod = new Rmethod(method, scopeId);
		variable.append(rmethod, paramIds, addVariable);
	}

}
