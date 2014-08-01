/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import sav.common.core.utils.StringUtils;
import tzuyu.engine.model.exception.TzRtExceptionType;
import tzuyu.engine.model.exception.TzRuntimeException;

/**
 * @author LLT
 * If the exception because of Assertion error, means it needs to be fixed.
 */
public class Assert {

	public static <T> void assertNotNull(T value, String... msgs) {
		assertTrue(value != null, msgs);
	}

	public static <T> void assertTrue(boolean condition, String... msgs) {
		if (!condition) {
			String msg = StringUtils.EMPTY;
			if (msgs != null) {
				msg = StringUtils.join(", ", (Object[]) msgs);
			}
			throw new TzRuntimeException(TzRtExceptionType.AssertException, msg);
		}
	}

	public static void assertFail(String msg) {
		throw new TzRuntimeException(msg);
	}

}
