/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.type.IType;
import gentest.core.data.variable.GeneratedVariable;
import gentest.main.GentestConstants;
import sav.common.core.SavException;
import sav.common.core.utils.Randomness;

/**
 * @author Nguyen Phuoc Nguong Phuc
 */
public class ArrayValueGenerator extends ValueGenerator {
	private int dimension;
	private IType lastContenType;
	
	public ArrayValueGenerator(IType type) {
		super(type);
		dimension = getArrayDimension();
		lastContenType = getLastContentType();
	}

	@Override
	public boolean doAppendVariable(GeneratedVariable variable, int level) throws SavException {
		// Generate the array
		int sizes[] = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			sizes[i] = Randomness
					.nextInt(GentestConstants.VALUE_GENERATION_ARRAY_MAXLENGTH);
		}
		final RArrayConstructor arrayConstructor = new RArrayConstructor(sizes,
				type.getRawType(), lastContenType.getRawType());
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		// Generate the array content
		int[] location = next(null, arrayConstructor.getSizes());
		while (location != null) {
			GeneratedVariable newVar = appendVariable(variable,
					level + 1, lastContenType);
			int localVariableID = newVar.getReturnVarId(); // the last ID

			// get the variable
			RArrayAssignment arrayAssignment = new RArrayAssignment(
					arrayConstructor.getOutVarId(), location, localVariableID);
			variable.append(arrayAssignment);
			location = next(location, arrayConstructor.getSizes());
		}
		return true;
	}
	
	private IType getLastContentType() {
		IType contentType = type;
		while (contentType.isArray()) {
			contentType = contentType.getComponentType();
		}
		return contentType;
	}

	private int getArrayDimension() {
		int dimension = 0;
		IType arrayType = type;
		while (arrayType.isArray()) {
			arrayType = arrayType.getComponentType();
			dimension++;
		}
		return dimension;
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
