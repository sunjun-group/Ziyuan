/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.typeinitilizer;

import gentest.core.data.MethodCall;
import gentest.main.GentestConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.iface.HasProbabilityType;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 * 
 */
public class TypeInitializer {
	private Class<?> type;
	private Map<ConstructorType, List<Object>> constructors;
	
	public TypeInitializer(Class<?> type) {
		this.type = type;
		constructors = new HashMap<TypeInitializer.ConstructorType, List<Object>>();
	}

	public boolean hasNoConstructor() {
		return constructors.isEmpty();
	}
	
	public void addConstructors(List<Constructor<?>> constructors) {
		for (Constructor<?> constructor : constructors) {
			ConstructorType type = ConstructorType.NO_PARAM_CONSTRUCTOR;
			if (CollectionUtils.isEmpty(constructor.getParameterTypes())) {
				type = ConstructorType.VISIBLE_CONSTRUCTOR;
			}
			addToConstructors(type, constructor);
		}
	}
	
	public void addConstructor(Constructor<?> constructor, boolean hasParam) {
		ConstructorType type = ConstructorType.NO_PARAM_CONSTRUCTOR;
		if(hasParam) {
			type = ConstructorType.VISIBLE_CONSTRUCTOR;
		}
		addToConstructors(type, constructor);
	}
	
	private void addToConstructors(ConstructorType type, Object constructor) {
		CollectionUtils.getListInitIfEmpty(constructors, type)
				.add(constructor);
	}
	
	
	public void addNoParamConstructors(Constructor<?> constructor) {
		addToConstructors(ConstructorType.NO_PARAM_CONSTRUCTOR, constructor);
	}

	public void addOtherConstructors(Constructor<?> constructor) {
		addToConstructors(ConstructorType.VISIBLE_CONSTRUCTOR, constructor);
	}

	public void addStaticMethod(Method method) {
		addToConstructors(ConstructorType.STATIC_METHODS, method);
	}
	
	public void addBuilderMethodCall(MethodCall methodCall) {
		addToConstructors(ConstructorType.BUILDER_METHOD_CALL, methodCall);
	}
	
	public Object getRandomConstructor() {
		if (constructors.isEmpty()) {
			return null;
		}
		Set<ConstructorType> keys = constructors.keySet();
		ConstructorType key = Randomness.randomWithDistribution(keys
				.toArray(new ConstructorType[keys.size()]));
		return Randomness.randomMember(constructors.get(key));
	}

	public static enum ConstructorType implements HasProbabilityType {
		NO_PARAM_CONSTRUCTOR (GentestConstants.PROBABILITY_OF_PUBLIC_NO_PARAM_CONSTRUCTOR),
		VISIBLE_CONSTRUCTOR (GentestConstants.PROBABILITY_OF_PUBLIC_CONSTRUCTOR),
		STATIC_METHODS (GentestConstants.PROBABILITY_OF_STATIC_METHOD_INIT),
		BUILDER_METHOD_CALL (GentestConstants.PROBABILITY_OF_BUILDER_METHOD_CALL_INIT);
		
		private int prob;
		private ConstructorType(int prob) {
			this.prob = prob;
		}
		
		public int getProb() {
			return prob;
		}
	}

	public Class<?> getType() {
		return type;
	}
}
