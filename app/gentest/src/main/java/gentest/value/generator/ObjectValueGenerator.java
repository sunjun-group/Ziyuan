/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import extcos.ReferenceAnalyser;

import gentest.data.statement.RConstructor;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class ObjectValueGenerator extends AbstractValueGenerator {
	private ReferenceAnalyser refAnalyzer = new ReferenceAnalyser();
	
	@Override
	public void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		Member initializedStmt = findConstructor(type);
		if (initializedStmt instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) initializedStmt;
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
		} else if (initializedStmt instanceof Method) {
			doAppendStaticMethods(variable, level,
					CollectionUtils.listOf((Method) initializedStmt));
		} else {
			assignNull(variable, type);
		}
		/* we accept to init null for the obj*/
	}

	private Member findConstructor(Class<?> type) {
		try {
			/* try with the perfect one which is public constructor with */
			Constructor<?> constructor = type.getConstructor();
			if (isAccessibleAndPublic(constructor)) {
				return constructor;
			}
		} catch (Exception e) {
			// do nothing, just keep trying. 
		}
		List<Constructor<?>> candidates = new ArrayList<Constructor<?>>();
		for (Constructor<?> constructor : type.getConstructors()) {
			if (isAccessibleAndPublic(constructor)) {
				candidates.add(constructor);
			}
		}
		if (!candidates.isEmpty()) {
			return Randomness.randomMember(candidates);
		}
		
		// check if it is interface or abstract
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			return findConstructor(refAnalyzer.getRandomImplClzz(type));
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
		// cannot find constructor;
		return null;
	}

	private boolean isAccessibleAndPublic(Constructor<?> constructor) {
		return Modifier.isPublic(constructor.getModifiers());
	}

}
