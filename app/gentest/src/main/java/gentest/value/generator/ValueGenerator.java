/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import gentest.data.statement.RAssignment;
import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public abstract class ValueGenerator {
	/* level of generated value from root statment
	 * ex: generate value for parameter p1 of method:
	 * methodA(List<Interger> p1, p2)
	 * we do 2 generation step:
	 * generate list -> level 1
	 * generate values for list -> level 2
	 * */
	protected static int maxLevel = 10;
	// NOTE This must be less than 255
	public static final int GENERATED_ARRAY_MAX_LENGTH = 10;

	public static GeneratedVariable generate(Class<?> clazz, Type type, int firstStmtIdx,
			int firstVarId) throws SavException {
		GeneratedVariable variable = new GeneratedVariable(firstStmtIdx,
				firstVarId);
		append(variable, 1, clazz, type);
		return variable;
	}

	public static void append(GeneratedVariable variable, int level,
			Class<?> clazz, Type type) throws SavException {
		if (PrimitiveValueGenerator.accept(clazz)) {
			new PrimitiveValueGenerator().doAppend(variable, level, clazz);
			return;
		} 
		if (level > maxLevel) {
			assignNull(variable, clazz);
			return;
		}

		ValueGenerator generator = findGenerator(clazz, type);
		generator.doAppend(variable, level, clazz);
	}

	protected static void assignNull(GeneratedVariable variable, Class<?> clazz) {
		variable.append(RAssignment.assignmentFor(clazz, null));
	}
	
	protected final void doAppendMethods(GeneratedVariable variable, int level,
			int scopeId, List<Method> methodcalls) throws SavException {
		doAppendMethods(variable, level, methodcalls, scopeId, false);
	}

	protected final void doAppendStaticMethods(GeneratedVariable variable, int level,
			List<Method> methodcalls) throws SavException {
		doAppendMethods(variable, level, methodcalls, Statement.INVALID_VAR_ID,
				true);
	}
	
	private void doAppendMethods(GeneratedVariable variable, int level, 
			List<Method> methodcalls, int scopeId, boolean addVariable) throws SavException {
		// generate value for method call
		for (Method method : methodcalls) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			int[] paramIds = new int[parameterTypes.length];
			for (int i = 0; i < paramIds.length; i++) {
				Class<?> paramType = parameterTypes[i];
				ValueGenerator.append(variable, level + 2, paramType, null);
				paramIds[i] = variable.getLastVarId();
			}
			Rmethod rmethod = new Rmethod(method, scopeId);
			variable.append(rmethod, paramIds, addVariable);
		}
	}

	public abstract void doAppend(GeneratedVariable variable, int level,
			Class<?> type) throws SavException;

	private static ValueGenerator findGenerator(Class<?> clazz, Type type) {
		if (clazz.isArray()) {
			return new ArrayValueGenerator();
		}
		if (ListValueGenerator.accept(clazz)) {
			return new ListValueGenerator(type);
		}
		if (SetValueGenerator.accept(clazz)) {
			return new SetValueGenerator(type);
		}
		if (MapValueGenerator.accept(clazz)) {
			return new MapValueGenerator(type);
		}
		return getGenerator(ObjectValueGenerator.class);
	}
	
	protected static ValueGenerator getGenerator(Class<?> clazz) {
		if (clazz == ObjectValueGenerator.class) {
			return new ObjectValueGenerator();
		}
		return null;
	}
}
