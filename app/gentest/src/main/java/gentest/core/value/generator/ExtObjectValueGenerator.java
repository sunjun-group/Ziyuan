/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import static gentest.main.GentestConstants.*;
import gentest.core.commons.utils.MethodUtils;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.execution.VariableRuntimeExecutor;
import gentest.core.value.AccesibleObjectVerifier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 *
 */
public class ExtObjectValueGenerator extends ObjectValueGenerator {
	private List<Method> methodcalls;
	
	public ExtObjectValueGenerator(IType type, List<String> methodSigns) {
		super(type);
		initMethodCalls(methodSigns);
	}

	private void initMethodCalls(List<String> methodSigns) {
		List<Method> initMethods;
		if (methodSigns == null) {
			initMethods = getCandidatesMethodForObjInit(type.getRawType());
		} else {
			initMethods = MethodUtils.findMethods(type.getRawType(), methodSigns);
		}
		methodcalls = new ArrayList<Method>(
				Randomness.randomSequence(initMethods, OBJECT_VALUE_GENERATOR_MAX_SELECTED_METHODS));
	}

	/**
	 * select from declared method list only methods which 
	 * - do not contain excluded method prefix
	 * - are public methods
	 */
	private List<Method> getCandidatesMethodForObjInit(Class<?> targetClazz) {
		Method[] declaredMethods = targetClazz.getDeclaredMethods();
		List<Method> methods = new ArrayList<Method>(declaredMethods.length);
		for (Method method : declaredMethods) {
			/* ignore static method */
			if (MethodUtils.isStatic(method)) {
				continue;
			}
			if (!AccesibleObjectVerifier.verify(method, method.getParameterTypes()) || 
					method.isBridge()) {
				continue;
			}
			if (MethodUtils.isPublic(method) && !doesContainExcludedMethodPrefix(method)) {
				methods.add(method);
			}
		}
		return methods;
	}

	private boolean doesContainExcludedMethodPrefix(Method method) {
		for (String excludePref : OBJ_INIT_EXCLUDED_METHOD_PREFIXIES) {
			if (method.getName().startsWith(excludePref)) {
				return true;
			}
		}
		return false;
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
		// executor.start(null);
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
				// executor.start(null);
				executor.execute(variable);
			}
		}
	}

}
