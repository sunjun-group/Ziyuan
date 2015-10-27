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
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.store.iface.ITypeMethodCallStore;

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
	protected IType type;
	protected ValueGeneratorMediator valueGeneratorMediator;
	
	protected ValueGenerator(IType type) {
		this.type = type;
	}

	public static void assignNull(GeneratedVariable variable, Class<?> clazz) {
		variable.append(RAssignment.assignmentFor(clazz, null));
	}
	
	protected GeneratedVariable appendVariable(GeneratedVariable rootVariable, int level,
			IType type) throws SavException {
		return getValueGeneratorMediator().append(rootVariable, level, type);
	}

	public abstract boolean doAppendVariable(GeneratedVariable variable, int level)
			throws SavException;

	public static ValueGenerator findGenerator(IType type, boolean isReceiver) {
		if (type.isArray()) {
			return new ArrayValueGenerator(type);
		}
		Pair<Class<?>, List<String>> typeDef = specificObjectMap.get(type.getRawType());
		if (typeDef != null) {
			return new ExtObjectValueGenerator(type.resolveType(typeDef.a),
					typeDef.b);
		}
		// even receiver, allow to append method calls after initializing
//		if (isReceiver) {
//			return new ObjectValueGenerator(type);
//		}
		return new ExtObjectValueGenerator(type, null);
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
