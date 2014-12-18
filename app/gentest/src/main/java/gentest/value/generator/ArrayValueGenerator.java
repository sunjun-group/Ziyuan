/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import gentest.data.statement.RArrayAssignment;
import gentest.data.statement.RArrayConstructor;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;

/**
 * @author LLT
 * 
 */
public class ArrayValueGenerator extends AbstractValueGenerator {

	@Override
	public void doAppend(GeneratedVariable variable, int level, Class<?> type)
			throws SavException {
		// Generate the array
		final int dimension = 1 + type.getName().lastIndexOf('[');
		final RArrayConstructor arrayConstructor = new RArrayConstructor(
				dimension, type);
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		// Generate the array content
		int[] location = next(null, arrayConstructor.getSizes());
		while (location != null) {
			GeneratedVariable newVariable = variable;
			AbstractValueGenerator.append(newVariable, level + 1, arrayConstructor.getContentType(),
					null);
<<<<<<< HEAD
			int localVariableID = newVariable.getLastVarId(); // the last ID
=======
			int localVariableID = variable.getLastVarId(); // the last ID
>>>>>>> a42c68466e63b0d5aac5005c9faed9a146b69e50
			// get the variable
			RArrayAssignment arrayAssignment = new RArrayAssignment(
					arrayConstructor.getOutVarId(), location, localVariableID);
			newVariable.append(arrayAssignment);
			location = next(location, arrayConstructor.getSizes());
		}
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
