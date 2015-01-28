/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LLT
 * 
 */
public class CollectionUtils {
	private CollectionUtils() {
	}

	public static <T> List<T> listOf(T value) {
		List<T> list = new ArrayList<T>();
		list.add(value);
		return list;
	}
	
	public static <T> List<T> toArrayList(T[] vals) {
		List<T> list = new ArrayList<T>();
		for (T val : vals) {
			list.add(val);
		}
		return list;
	}
	
	public static <T> List<T> join(List<T>... lists) {
		List<T> result = new ArrayList<T>();
		for (List<T> list : lists) {
			result.addAll(list);
		}
		return result;
	}

	public static <T> T getFirstElement(T[] vals) {
		if (isEmptyCheckNull(vals)) {
			return null;
		}
		return vals[0];
	}

	public static <T> boolean existIn(T val, T... valList) {
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

	public static <T> void addIfNotNullNotExist(Collection<T> col, T[] arr) {
		if (isEmpty(arr)) {
			return;
		}
		for (T val : arr) {
			addIfNotNullNotExist(col, val);
		}
	}
	
	public static <T> boolean isNotEmpty(Collection<T> values) {
		return ! isEmpty(values);
	}

	public static <T> boolean isEmpty(T[] vals) {
		return vals == null || vals.length == 0;
	}
	
	public static <T> boolean isNotEmpty(T[] vals) {
		return !isEmpty(vals);
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
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public static <T> List<T> nullToEmpty(List<T> val) {
		if (val == null) {
			return new ArrayList<T>();
		}
		return val;
	}

	public static <K, E> List<E> getListInitIfEmpty(Map<K, List<E>> map, K key) {
		List<E> value = map.get(key);
		if (value == null) {
			value = new ArrayList<E>();
			map.put(key, value);
		}
		return value;
	}
	
	public static <K, E> Set<E> getSetInitIfEmpty(Map<K, Set<E>> map, K key) {
		Set<E> value = map.get(key);
		if (value == null) {
			value = new HashSet<E>();
			map.put(key, value);
		}
		return value;
	}

	public static <T> void filter(Iterable<T> unfiltered, Predicate<T> predicate) {
		for (Iterator<T> it = unfiltered.iterator(); it.hasNext();) {
			if (!predicate.apply(it.next())) {
				it.remove();
			}
		}
	}

	public static <T>List<T> emptyToNull(List<T> list) {
		if (isEmpty(list)) {
			return null;
		}
		return list;
	}
	
	public static <T>List<T> copy(List<T> list) {
		return new ArrayList<T>(nullToEmpty(list));
	}
	
	public static <T> T getLast(List<T> list) {
		if (isEmpty(list)) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	/**
	 * fromIndex, inclusive, 
	 * and toIndex, exclusive
	 */
	public static <E> void removeLastElements(List<E> list, int fromIdx) {
		list.subList(fromIdx, list.size()).clear();
	}
	
	public static <E> void removeLast(List<E> list) {
		if (isEmpty(list)) {
			return;
		}
		list.remove(list.size() - 1);
	}
}
