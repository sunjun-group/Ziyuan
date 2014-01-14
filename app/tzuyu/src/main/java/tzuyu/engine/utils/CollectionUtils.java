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

	public static <T> boolean existIn(T val, T... valList) {
		return existInArray(val, valList);
	}
	
	public static <T> boolean existInArray(T val, T[] valList) {
		for (T valInList : valList) {
			if (val.equals(valInList)) {
				return true;
			}
		}
		return false;
	}
	
	public static <T> boolean isEmpty(T[] vals) {
		return vals == null || vals.length == 0;
	}
}
