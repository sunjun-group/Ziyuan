/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import java.util.Collection;


/**
 * @author LLT
 *
 */
public class CollectionUtils {
	private CollectionUtils() {}
	
	public static <T> T getFirstElement(T[] vals) {
		if (isEmptyCheckNull(vals)) {
			return null;
		}
		return vals[0];
	}

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
	
	public static <T> void addIfNotNull(Collection<T> col, T val) {
		if (val != null) {
			col.add(val);
		}
	}
	
	public static <T> boolean isEmpty(T[] vals) {
		return vals == null || vals.length == 0;
	}
	
	public static <T> boolean isEmptyCheckNull(T[] vals) {
		return isEmpty(vals, true);
	}
	
	public static <T> boolean isEmpty(T[] vals, boolean checkNullVal) {
		boolean isEmpty = vals == null || vals.length == 0;
		if (isEmpty) {
			return true;
		}
		if (checkNullVal) {
			for (T val : vals) {
				if (val != null) {
					return false;
				}
			}
			isEmpty = true;
		}
		return isEmpty;
	}

	public static boolean isEmpty(Collection<?> col) {
		return col == null || col.isEmpty();
	}

}
