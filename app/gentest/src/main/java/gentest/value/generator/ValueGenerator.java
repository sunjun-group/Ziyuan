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
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ValueGenerator {

	public static GeneratedVariable generate(Class<?> clazz, Type type, 
			int firstVarId, boolean isReceiver) throws SavException {
		GeneratedVariable variable = new GeneratedVariable(firstVarId);
		return append(variable, 1, clazz, type, isReceiver);
	}

	private static VariableCache getVariableCache() {
		return VariableCache.getInstance();
	}
	
	public static GeneratedVariable append(GeneratedVariable rootVariable, int level,
			Class<?> clazz, Type type) throws SavException {
		return append(rootVariable, level, clazz, type, false);
	}

	public static GeneratedVariable append(GeneratedVariable rootVariable, int level,
			Class<?> clazz, Type type, boolean isReceiver) throws SavException {
		GeneratedVariable variable = null;
		boolean selectFromCache = Randomness
				.weighedCoinFlip(GentestConstants.CACHE_VALUE_PROBABILITY);
		if (selectFromCache) {
			/* trying to lookup in cache */
			variable = getVariableCache().select(type, clazz);
			if (variable != null) {
				int toVarId = variable.getNewVariables().size();
				int toStmtIdx = variable.getStmts().size();
				if (variable.getObjCuttingPoints() != null) {
					int[] stopPoint = Randomness.randomMember(variable
							.getObjCuttingPoints());
					toVarId = stopPoint[0];
					toStmtIdx = stopPoint[1];
				}
				variable = variable.duplicate(rootVariable.getNextVarId(),
						toVarId, toStmtIdx);
			}
		}
		
		if (variable == null) {
			variable = rootVariable.newVariable();
			/* generate the new one*/
			if (PrimitiveValueGenerator.accept(clazz)) {
				new PrimitiveValueGenerator().doAppend(variable, level, clazz);
			}  else if (level > GentestConstants.VALUE_GENERATION_MAX_LEVEL) {
				assignNull(variable, clazz);
			} else {
				ValueGenerator generator = findGenerator(clazz, type, isReceiver);
				generator.doAppend(variable, level, clazz);
			}
			getVariableCache().put(type, clazz, variable);
		}
		rootVariable.append(variable);
		return variable;
	}

	protected static void assignNull(GeneratedVariable variable, Class<?> clazz) {
		variable.append(RAssignment.assignmentFor(clazz, null));
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
			GeneratedVariable newVar = ValueGenerator.append(variable,
					level + 2, paramType.a, paramType.b);
			paramIds[i] = newVar.getReturnVarId();
		}
		Rmethod rmethod = new Rmethod(method, scopeId);
		variable.append(rmethod, paramIds, addVariable);
	}

	protected Pair<Class<?>, Type> getParamType(Class<?> clazz, Type type) {
		return new Pair<Class<?>, Type>(clazz, null);
	}

	public abstract boolean doAppend(GeneratedVariable variable, int level,
			Class<?> type) throws SavException;

	private static ValueGenerator findGenerator(Class<?> clazz, Type type,
			boolean isReceiver) {
		if (clazz.isArray()) {
			return new ArrayValueGenerator(type);
		}
		Pair<Class<?>, List<String>> typeDef = specificObjectMap.get(clazz);
		if (typeDef != null) {
			return new ExtObjectValueGenerator(typeDef.a, type, 
					typeDef.b);
		}
		if (isReceiver) {
			return new ObjectValueGenerator();
		}
		return new ExtObjectValueGenerator(clazz, type, null);
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
	
}
