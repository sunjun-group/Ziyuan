/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import static sav.common.core.utils.CollectionUtils.listOf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gentest.core.data.statement.RAssignment;
import gentest.core.data.type.ISubTypesScanner;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.store.iface.ITypeInitializerStore;
import gentest.core.value.store.iface.ITypeMethodCallStore;
import gentest.main.GentestConstants;
import gentest.utils.SignatureUtils;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

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
		Class<?> rawType = type.getRawType();
		Pair<Class<?>, List<String>> typeDef = loadSpecialObjDescription(rawType);
		if (typeDef != null) {
			return new ExtObjectValueGenerator(type.resolveType(typeDef.a),
					typeDef.b);
		}
		// comment following condition will call method multiple times
		if (ignoreMethodCalls.contains(rawType)) {
			return new ObjectValueGenerator(type);
		} else if (isReceiver) {
			/* select simple setter methods */
			List<String> setterMethods = new ArrayList<>();
			for (Method method : type.getRawType().getMethods()) {
				if (method.getName().startsWith("set")) {
					setterMethods.add(SignatureUtils.createMethodNameSign(method));
				}
			}
			return new ExtObjectValueGenerator(type, setterMethods, GentestConstants.RECEIVER_EXT_METHODS_LIMIT);
		} else {
			return new ExtObjectValueGenerator(type, null);
		}
	}

	private static Pair<Class<?>, List<String>> loadSpecialObjDescription(Class<?> rawType) {
		String type = rawType.getName();
		if (Collection.class.getName().equals(type)) {
			type = Randomness.randomMember(new String[]{List.class.getName(), Set.class.getName()});
		}
		return specificObjectMap.get(type);
	}
	
	private static Map<String, Pair<Class<?>, List<String>>> specificObjectMap;
	private static List<Class<?>> ignoreMethodCalls;
	static {
		specificObjectMap = new HashMap<String, Pair<Class<?>,List<String>>>();
		specificObjectMap.put(List.class.getName(), new Pair(ArrayList.class, listOf("add(Ljava/lang/Object;)Z")));
		specificObjectMap.put(Set.class.getName(), new Pair(HashSet.class, listOf("add(Ljava/lang/Object;)Z")));
		specificObjectMap.put(Map.class.getName(),
									new Pair(HashMap.class,
											listOf("put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")));
		ignoreMethodCalls = new ArrayList<Class<?>>();
		ignoreMethodCalls.add(Thread.class);
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
	
	protected ITypeInitializerStore getTypeInitializerStore() {
		return valueGeneratorMediator.getTypeInitializerStore();
	}

	public void setValueGeneratorMediator(
			ValueGeneratorMediator valueGeneratorMediator) {
		this.valueGeneratorMediator = valueGeneratorMediator;
	}
	
	public IRandomness getRandomness() {
		return valueGeneratorMediator.getRandomness();
	}
}
