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
import sav.common.core.utils.PrimitiveUtils;
import sav.common.core.utils.Randomness;

/**
 * @author Nguyen Phuoc Nguong Phuc
 */
public class ArrayValueGenerator extends ValueGenerator {
	private int dimension; // start from 1
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
			sizes[i] = selectArraySize(i + 1, dimension, lastContenType.getRawType(), level);
		}
		final RArrayConstructor arrayConstructor = new RArrayConstructor(sizes,
				type.getRawType(), lastContenType.getRawType());
		variable.append(arrayConstructor);
		variable.commitReturnVarIdIfNotExist();
		// Generate the array content
		int[] location = ArrayRandomness.next(null, arrayConstructor.getSizes()); // if arrayConstructor.getSizes()[i]>0, i.e. size[i]>0, location[i] = 0,otherwise null
		while (location != null) {
			/* keep level the same for better chance to generate a non-null value for content, this would be make
			 * more sense in case of an array.
			 */
			GeneratedVariable newVar = appendVariable(variable,
					level, lastContenType);
			int localVariableID = newVar.getReturnVarId(); // the last ID

			// get the variable
			RArrayAssignment arrayAssignment = new RArrayAssignment(
					arrayConstructor.getOutVarId(), location, localVariableID);
			variable.append(arrayAssignment);
			location = ArrayRandomness.next(location, arrayConstructor.getSizes());
		}
		return true;
	}

	/**
	 * maxsize of the last dimension = 10 (if primitive/String) or 1 -> 3 (if not primitive), and is decreased by dimension.
	 * maxsize of other dimension not the last one is from 1 -> 2.
	 * */
	private int selectArraySize(int curLevel, int dimension, Class<?> contentType, int varLevel) {
		if ((curLevel != dimension && dimension > 1) || varLevel >= 4) {
			return Randomness.nextInt(1, 2);
		}
		
		String clazzName = contentType.getName();
		boolean isSimpleType = PrimitiveUtils.isSimpleType(clazzName);
		int maxSize = GentestConstants.VALUE_GENERATION_ARRAY_MAXLENGTH;
		
		if (!isSimpleType && maxSize > 5) {
			maxSize = getRandomness().weighedCoinFlip(0.7) ? 3 : 5;
		}
		if (curLevel == dimension && dimension > 1) {
			maxSize = maxSize - (dimension * 2) + 1;
			if (maxSize <= 0) {
				maxSize = 2;
			}
		} 
		return Randomness.nextInt(1, maxSize);
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



}
