/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import gentest.ParamGeneratorFactory;
import gentest.commons.utils.GenTestUtils;
import gentest.commons.utils.TypeUtils;
import gentest.data.statement.RAssignment;
import gentest.data.variable.GeneratedVariable;

import java.lang.reflect.Type;

import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class PrimitiveValueGenerator {
	protected static ParamGeneratorFactory generatorFactory = ParamGeneratorFactory.getInstance();
	private PrimitiveValueGenerator() {
		
	}
	
	public static boolean accept(Class<?> clazz, Type type) {
		return TypeUtils.isPrimitive(clazz) 
				|| TypeUtils.isPrimitiveObject(clazz)
				|| TypeUtils.isString(clazz) 
				|| TypeUtils.isEnumType(clazz)
				|| (isObject(clazz) && type == null);

	}

	public static boolean doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		type = GenTestUtils.toClassItselfOrItsDelegate(type);
		Object value = generatorFactory.getGeneratorFor(type).next();
		variable.append(RAssignment.assignmentFor(type, value));
		return true;
	}

	private static boolean isObject(Class<?> clazz) {
		return Object.class.equals(clazz);
	}
}
