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
	private Map<ConstructorType, List<Object>> constructorsMap;
	/* bad constructors are only stored if there are no other good options found */
	private List<Object> badConstructors;
	
	public TypeInitializer(Class<?> type) {
		this.type = type;
		constructorsMap = new HashMap<TypeInitializer.ConstructorType, List<Object>>();
	}

	public boolean hasNoConstructor() {
		return constructorsMap.isEmpty();
	}
	
	public void addBadConstructors(List<Object> constructors) {
		badConstructors = CollectionUtils.initIfEmpty(badConstructors);
		badConstructors.addAll(constructors);
	}
	
	public void addConstructor(Constructor<?> constructor, boolean hasParam) {
		if(hasParam) {
			addParamConstructors(constructor);
		} else {
			addNoParamConstructors(constructor);
		}
	}
	
	private void addToConstructors(ConstructorType type, Object constructor, int initListSize) {
		CollectionUtils.getListInitIfEmpty(constructorsMap, type, initListSize)
				.add(constructor);
	}
	
	
	public void addNoParamConstructors(Constructor<?> constructor) {
		addToConstructors(ConstructorType.NO_PARAM_CONSTRUCTOR, constructor, 1);
	}

	public void addParamConstructors(Constructor<?> constructor) {
		addToConstructors(ConstructorType.VISIBLE_CONSTRUCTOR, constructor, 5);
	}

	public void addStaticMethod(Method method) {
		addToConstructors(ConstructorType.STATIC_METHODS, method, 5);
	}
	
	public void addBuilderMethodCall(MethodCall methodCall) {
		addToConstructors(ConstructorType.BUILDER_METHOD_CALL, methodCall, 5);
	}
	
	public Object getRandomConstructor() {
		if (constructorsMap.isEmpty()) {
			/* random from bad constructors */
			return Randomness.randomMember(badConstructors);
		}
		Set<ConstructorType> keys = constructorsMap.keySet();
		ConstructorType key = Randomness.randomWithDistribution(keys
				.toArray(new ConstructorType[keys.size()]));
		return Randomness.randomMember(constructorsMap.get(key));
	}

	public static enum ConstructorType implements HasProbabilityType {
		/* public, no param */
		NO_PARAM_CONSTRUCTOR (GentestConstants.PROBABILITY_OF_PUBLIC_NO_PARAM_CONSTRUCTOR),
		/* public, params */
		VISIBLE_CONSTRUCTOR (GentestConstants.PROBABILITY_OF_PUBLIC_CONSTRUCTOR),
		/* static method */
		STATIC_METHODS (GentestConstants.PROBABILITY_OF_STATIC_METHOD_INIT),
		/* static method of inner class */
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
