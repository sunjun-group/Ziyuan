/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.commons.utils.TypeUtils;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.variable.GeneratedVariable;
import gentest.core.value.generator.PrimitiveGeneratorFactory.PrimitiveGenerator;
import gentest.main.GentestConstants;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author LLT
 *
 */
@Singleton
public class PrimitiveValueGenerator {
	@Inject
	private PrimitiveGeneratorFactory generatorFactory;
	
	public static boolean accept(Class<?> clazz) {
		return TypeUtils.isPrimitive(clazz) 
				|| TypeUtils.isPrimitiveObject(clazz)
				|| TypeUtils.isString(clazz) 
				|| TypeUtils.isEnumType(clazz)
				|| (isObject(clazz));

	}

	public boolean doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		if (Object.class.equals(type)) {
			type = Randomness
					.randomMember(GentestConstants.DELEGATING_CANDIDATES_FOR_OBJECT);
		}
		Object value = null;
		PrimitiveGenerator<?> generator = generatorFactory.getGenerator(type);
		if (generator != null) {
			value = generator.next(type);
		} else {
			value = generatorFactory.getGenerator(type).next();
		}
		variable.append(RAssignment.assignmentFor(type, value));
		return true;
	}

	private static boolean isObject(Class<?> clazz) {
		return Object.class.equals(clazz);
	}
		
	public void setGeneratorFactory(PrimitiveGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}
}
