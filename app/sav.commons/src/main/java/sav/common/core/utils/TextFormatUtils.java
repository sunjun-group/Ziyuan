/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author LLT
 * 
 */
public class TextFormatUtils {
	private TextFormatUtils() {
	}
	
	public static <T>String printListSeparateWithNewLine(Collection<T> values) {
		return StringUtils.join(values, "\n");
	}
	
	public static <K, V>String printMap(Map<K, V> values) {
		if (CollectionUtils.isEmpty(values)) {
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (Entry<K, V> entry : values.entrySet()) {
			sb.append(entry.getKey()).append(" : ").append(entry.getValue())
				.append("\n");
		}
		return sb.toString();
	}
}
