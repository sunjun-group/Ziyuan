/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import static sav.common.core.utils.CollectionUtils.listOf;
import gentest.data.statement.RAssignment;
import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;
import gentest.data.variable.GeneratedVariable;
import gentest.value.VariableCache;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.GentestConstants;
import sav.common.core.Pair;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
@SuppressWarnings("rawtypes")
public abstract class ValueGenerator {

	public static GeneratedVariable generate(Class<?> clazz, Type type, 
			int firstVarId) throws SavException {
		GeneratedVariable variable = new GeneratedVariable(firstVarId);
		append(variable, 1, clazz, type);
		VariableCache.getInstance().put(clazz, variable);
		return variable;
	}

	public static void append(GeneratedVariable variable, int level,
			Class<?> clazz, Type type) throws SavException {
		if (PrimitiveValueGenerator.accept(clazz)) {
			new PrimitiveValueGenerator().doAppend(variable, level, clazz);
			return;
		} 
		if (level > GentestConstants.VALUE_GENERATION_MAX_LEVEL) {
			assignNull(variable, clazz);
			return;
		}

		ValueGenerator generator = findGenerator(clazz, type);
		generator.doAppend(variable, level, clazz);
	}

	protected static void assignNull(GeneratedVariable variable, Class<?> clazz) {
		variable.append(RAssignment.assignmentFor(clazz, null));
	}
	
	protected final void doAppendStaticMethods(GeneratedVariable variable, int level,
			List<Method> methodcalls) throws SavException {
		doAppendMethods(variable, level, methodcalls, Statement.INVALID_VAR_ID,
				true);
	}
	
	protected final void doAppendMethods(GeneratedVariable variable, int level, 
			List<Method> methodcalls, int scopeId, boolean addVariable) throws SavException {
		// generate value for method call
		for (Method method : methodcalls) {
			/* check generic types */
			Type[] genericParamTypes = method.getGenericParameterTypes();
			Class<?>[] types = method.getParameterTypes();
			int[] paramIds = new int[genericParamTypes.length];
			for (int i = 0; i < paramIds.length; i++) {
				Type type = genericParamTypes[i];
				Pair<Class<?>, Type> paramType = getParamType(types[i], type);
				GeneratedVariable newVar = variable.newVariable();
				ValueGenerator.append(newVar, level + 2, paramType.a,
						paramType.b);
				paramIds[i] = newVar.getLastVarId();
				variable.append(newVar);
			}
			Rmethod rmethod = new Rmethod(method, scopeId);
			variable.append(rmethod, paramIds, addVariable);
		}
	}

	protected Pair<Class<?>, Type> getParamType(Class<?> clazz, Type type) {
		return new Pair<Class<?>, Type>(clazz, null);
	}

	public abstract boolean doAppend(GeneratedVariable variable, int level,
			Class<?> type) throws SavException;

	private static ValueGenerator findGenerator(Class<?> clazz, Type type) {
		if (clazz.isArray()) {
			return new ArrayValueGenerator();
		}
		Pair<Class<?>, List<String>> typeDef = specificObjectMap.get(clazz);
		if (typeDef != null) {
			return new ExtObjectValueGenerator(typeDef.a, type, 
					typeDef.b);
		}
		return getGenerator(ObjectValueGenerator.class);
	}
	
	private static Map<Class<?>, Pair<Class<?>, List<String>>> specificObjectMap;
	static {
		specificObjectMap = new HashMap<Class<?>, Pair<Class<?>,List<String>>>();
		specificObjectMap.put(List.class, new Pair(ArrayList.class, listOf("add(Ljava/lang/Object;)Z")));
		specificObjectMap.put(Set.class, new Pair(HashSet.class, listOf("add(Ljava/lang/Object;)Z")));
		specificObjectMap.put(Map.class,
									new Pair(HashMap.class,
											listOf("put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")));
	}
	
	protected static ValueGenerator getGenerator(Class<?> clazz) {
		if (clazz == ObjectValueGenerator.class) {
			return new ObjectValueGenerator();
		}
		return null;
	}
}
