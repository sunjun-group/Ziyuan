/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

/**
 * @author LLT
 *
 */
public class ArrayRandomness {
	private ArrayRandomness(){}
	
	/**
	 * return next location of multi-dimension array in consecutive way.
	 */
	public static int[] next(int[] array, int[] limit) {
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
