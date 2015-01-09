/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import java.lang.reflect.Type;

import main.GentestConstants;
import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 * @author Nguyen Phuoc Nguong Phuc
 */
public class ArrayValueGenerator extends ValueGenerator {
	private Type type;
	
	public ArrayValueGenerator(Type type) {
		this.type = type;
	}

	@Override
	public boolean doAppend(GeneratedVariable variable, int level, Class<?> clazz)
			throws SavException {
		// Generate the array
		final int dimension = 1 + clazz.getName().lastIndexOf('[');
		Class<?> contentType = clazz;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}
		int sizes[] = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			sizes[i] = Randomness
					.nextRandomInt(GentestConstants.VALUE_GENERATION_ARRAY_MAXLENGTH);
		}
		final RArrayConstructor arrayConstructor = new RArrayConstructor(sizes,
				clazz, contentType);
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		// Generate the array content
		int[] location = next(null, arrayConstructor.getSizes());
		while (location != null) {
			GeneratedVariable newVar = ValueGenerator.append(variable,
					level + 1, arrayConstructor.getContentType(), null);
			int localVariableID = newVar.getReturnVarId(); // the last ID

			// get the variable
			RArrayAssignment arrayAssignment = new RArrayAssignment(
					arrayConstructor.getOutVarId(), location, localVariableID);
			variable.append(arrayAssignment);
			location = next(location, arrayConstructor.getSizes());
		}
		return true;
	}

	private static int[] next(int[] array, int[] limit) {
		if (array == null) {
			final int[] result = new int[limit.length];
			for (int i = 0; i < result.length; i++) {
				if (limit[i] > 0) {
					result[i] = 0;
				} else {
					return null;
				}
			}
			return result;
		}
		int i = 0;
		while (i < array.length && array[i] >= limit[i] - 1) {
			i++;
		}
		if (i >= array.length) {
			return null;
		} else {
			array[i]++;
			if (i - 1 >= 0 && array[i - 1] == limit[i - 1] - 1) {
				for (int j = 0; j < i; j++) {
					array[j] = 0;
				}
			}
			return array;
		}
	}

}
