/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import static sav.common.core.utils.CollectionUtils.listOf;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.VariableCache;
import gentest.main.GentestConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		List<GeneratedVariable> candidatesInCache = getVariableCache()
				.getVariableByType(type, clazz);
		boolean selectFromCache = Randomness
				.weighedCoinFlip(calculateProbToGetValFromCache(candidatesInCache.size()));
		if (selectFromCache) {
			/* trying to lookup in cache */
			variable = Randomness.randomMember(candidatesInCache);
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
			boolean goodVariable = false;
			variable = rootVariable.newVariable();
			/* generate the new one*/
			if (PrimitiveValueGenerator.accept(clazz, type)) {
				goodVariable = PrimitiveValueGenerator.doAppend(variable, level, clazz);
			}  else if (level > GentestConstants.VALUE_GENERATION_MAX_LEVEL) {
				assignNull(variable, clazz);
			} else {
				ValueGenerator generator = findGenerator(clazz, type, isReceiver);
				goodVariable = generator.doAppend(variable, level, clazz);
			}
			if (goodVariable) {
				getVariableCache().put(type, clazz, variable);
			}
		}
		rootVariable.append(variable);
		return variable;
	}

	public static double calculateProbToGetValFromCache(int varsSizeInCache) {
		double prob = GentestConstants.PROBABILITY_OF_CACHE_VALUE
				+ ((double) varsSizeInCache / GentestConstants.MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE)
					* (1 - GentestConstants.PROBABILITY_OF_CACHE_VALUE);
		if (prob == 1) {
			prob -= 0.1;
		}
		return prob;
	}

	protected static void assignNull(GeneratedVariable variable, Class<?> clazz) {
		variable.append(RAssignment.assignmentFor(clazz, null));
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
