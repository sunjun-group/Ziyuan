/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

import java.util.Arrays;

/**
 * @author LLT
 *
 */
public class ArrayWalker {
	private ArrayWalker(){}
	
	/**
	 * return next location of multi-dimension array in consecutive way.
	 */
	public static int[] next(int[] currentLocation, int[] arrayDimensionSize) {
		if (currentLocation == null) {
			final int[] firstLocation = new int[arrayDimensionSize.length];
			for (int i = 0; i < firstLocation.length; i++) {
				if (arrayDimensionSize[i] > 0) {
					firstLocation[i] = 0;
				} else {
					return null;
				}
			}
			return firstLocation;
		}
		int i = 0;
		while (i < currentLocation.length && currentLocation[i] >= arrayDimensionSize[i] - 1) {
			i++;
		}
		if (i >= currentLocation.length) {
			return null;
		} else {
			int[] nextLocation = Arrays.copyOf(currentLocation, currentLocation.length);
			nextLocation[i]++;
			if (i - 1 >= 0 && nextLocation[i - 1] == arrayDimensionSize[i - 1] - 1) {
				for (int j = 0; j < i; j++) {
					nextLocation[j] = 0;
				}
			}
			return nextLocation;
		}
	}
}
