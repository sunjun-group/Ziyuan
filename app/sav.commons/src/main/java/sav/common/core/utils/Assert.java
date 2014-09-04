/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;


/**
 * @author LLT
 * If the exception because of Assertion error, means it needs to be fixed.
 */
public class Assert {

	public static <T> void notNull(T value, String... msgs) {
		assertTrue(value != null, msgs);
	}

	public static <T> void assertTrue(boolean condition, String... msgs) {
		if (!condition) {
			String msg = StringUtils.EMPTY;
			if (msgs != null) {
				msg = StringUtils.spaceJoin((Object[]) msgs);
			}
			throw new IllegalArgumentException(msg);
		}
	}

	public static void fail(String msg) {
		throw new IllegalArgumentException(msg);
	}

}
