/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import tzuyu.engine.model.exception.ExceptionType;
import tzuyu.engine.model.exception.TzRuntimeException;

/**
 * @author LLT
 * 
 */
public class Assert {

	public static <T> void assertNotNull(T value, String... msgs) {
		assertTrue(value != null, msgs);
	}

	public static <T> void assertTrue(boolean condition, String... msgs) {
		if (!condition) {
			throw new TzRuntimeException(ExceptionType.AssertException,
					StringUtils.join(", ", (Object[]) msgs));
		}
	}
}
