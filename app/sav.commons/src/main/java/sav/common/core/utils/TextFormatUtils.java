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
import java.util.concurrent.TimeUnit;

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
	
	public static String printTimeString(long time) {
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		long diffSec = timeUnit.toSeconds(time);
		long diffMin = timeUnit.toMinutes(time);
		StringBuilder sb = new StringBuilder();
		sb.append(time).append(" ms");
		if (diffMin > 1) {
			sb.append("(").append(diffMin).append("m").append(")");
		} else if (diffSec > 1) {
			sb.append("(").append(diffSec).append("s").append(")");
		}
		return sb.toString();
	}
}
