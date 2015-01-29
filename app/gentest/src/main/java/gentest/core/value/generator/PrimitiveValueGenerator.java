/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.ParamGeneratorFactory;
import gentest.core.commons.utils.TypeUtils;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.variable.GeneratedVariable;
import gentest.main.GentestConstants;

import java.lang.reflect.Type;

import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

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
		if (Object.class.equals(type)) {
			type = Randomness
					.randomMember(GentestConstants.CANDIDATE_DELEGATES_FOR_OBJECT);
		}
		Object value = generatorFactory.getGeneratorFor(type).next();
		variable.append(RAssignment.assignmentFor(type, value));
		return true;
	}

	private static boolean isObject(Class<?> clazz) {
		return Object.class.equals(clazz);
	}
}
