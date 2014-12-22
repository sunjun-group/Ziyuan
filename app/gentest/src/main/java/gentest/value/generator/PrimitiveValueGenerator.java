/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import sav.common.core.SavException;
import gentest.ParamGeneratorFactory;
import gentest.commons.utils.TypeUtils;
import gentest.data.statement.RAssignment;
import gentest.data.variable.GeneratedVariable;

/**
 * @author LLT
 *
 */
public class PrimitiveValueGenerator extends ValueGenerator {
	protected ParamGeneratorFactory generatorFactory;
	
	public PrimitiveValueGenerator() {
		generatorFactory = ParamGeneratorFactory.getInstance();
	}
	
	public static boolean accept(Class<?> type) {
		return TypeUtils.isPrimitive(type) 
				|| TypeUtils.isPrimitiveObject(type)
				|| TypeUtils.isString(type) 
				|| TypeUtils.isEnumType(type);

	}

	@Override
	public void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		Object value = generatorFactory.getGeneratorFor(type).next();
		variable.append(RAssignment.assignmentFor(type, value));
	}

}
