/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import sav.common.core.Pair;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class MapValueGenerator extends SpecificValueGenerator {
	private Type type;
	
	public MapValueGenerator(Type type) {
		super(HashMap.class, null);
		this.type = type;
	}
	
	@Override
	protected void doAppendMethod(GeneratedVariable variable, int level,
			int varId) throws SavException {
		super.doAppendMethod(variable, level, varId);
	}
	
//	private Class<?> pickClassFromType(Type type) {
//			if (type instanceof ParameterizedType) {
//				Type compType = ((ParameterizedType) type).getActualTypeArguments()[0];
//				if (compType instanceof Class<?>) {
//					return new Pair<Class<?>, Type>((Class<?>) compType, null);
//				}
//			}
//		if (type instanceof  )
//	}

	public static boolean accept(Class<?> type) {
		return type.isAssignableFrom(Map.class);
	}
}
