/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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

	public static <T extends Object> boolean existIn(T val, T... valList) {
		return existInArray(val, valList);
	}
	
	public static <T extends Object> boolean existInArray(T val, T[] valList) {
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
	
	public static <T> void addIfNotNullNotExist(Collection<T> col, T val) {
		if (val != null && !col.contains(val)) {
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

	public static <T extends Object> T getWithoutRangeCheck(List<T> col, int i) {
		try {
			return col.get(i);
		} catch (IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public static <T>List<T> nullToEmpty(List<T> val) {
		if (val == null) {
			return new ArrayList<T>();
		}
		return val;
	}
}
