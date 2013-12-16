/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;


/**
 * @author LLT
 *
 */
public class CollectionUtils {
	private CollectionUtils() {}

	public static <T extends Enum<?>> boolean existIn(T val, T... valList) {
		return existInArray(val, valList);
	}
	
	public static <T extends Enum<?>> boolean existInArray(T val, T[] valList) {
		for (T valInList : valList) {
			if (val == valInList) {
				return true;
			}
		}
		return false;
	}
}
