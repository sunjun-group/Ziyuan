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
import gentest.core.value.store.iface.ITypeMethodCallStore;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.strategies.gentest.ISubTypesScanner;

/**
 * @author LLT
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ValueGenerator {
	protected ValueGeneratorMediator valueGeneratorMediator;

	public static void assignNull(GeneratedVariable variable, Class<?> clazz) {
		variable.append(RAssignment.assignmentFor(clazz, null));
	}
	
	protected GeneratedVariable appendVariable(GeneratedVariable rootVariable, int level,
			Class<?> clazz, Type type) throws SavException {
		return getValueGeneratorMediator().append(rootVariable, level, clazz, type);
	}
	
	protected Pair<Class<?>, Type> getParamType(Class<?> clazz, Type type) {
		return new Pair<Class<?>, Type>(clazz, null);
	}

	public abstract boolean doAppend(GeneratedVariable variable, int level,
			Class<?> type) throws SavException;

	public static ValueGenerator findGenerator(Class<?> clazz, Type type,
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
	
	protected ISubTypesScanner getSubTypesScanner() {
		return valueGeneratorMediator.getSubTypeScanner();
	}
	
	public ValueGeneratorMediator getValueGeneratorMediator() {
		return valueGeneratorMediator;
	}
	
	protected ITypeMethodCallStore getTypeMethodCallsStore() {
		return valueGeneratorMediator.getTypeMethodCallsStore();
	}

	public void setValueGeneratorMediator(
			ValueGeneratorMediator valueGeneratorMediator) {
		this.valueGeneratorMediator = valueGeneratorMediator;
	}
	
}
