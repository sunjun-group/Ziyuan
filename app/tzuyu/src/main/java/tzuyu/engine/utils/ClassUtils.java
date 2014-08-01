/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

import sav.common.core.utils.StringUtils;


/**
 * @author LLT
 *
 */
public class ClassUtils {
	private ClassUtils() {}
	
	public static String getClassNameWithSuffix(Class<?> clazz) {
		Assert.assertNotNull(clazz);
		return StringUtils.join(".", clazz.getSimpleName(), "class");
	}
	
	public static String getSimpleCompilableName(Class<?> cls) {
		String retval = cls.getSimpleName();

		// If it's an array, it starts with "[".
		if (retval.charAt(0) == '[') {
			// Class.getName() returns a a string that is almost in JVML
			// format, except that it slashes are periods. So before calling
			// classnameFromJvm, we replace the period with slashes to
			// make the string true JVML.
			retval = Types.getTypeName(retval.replace('.', '/'));
		}

		// If inner classes are involved, Class.getName() will return
		// a string with "$" characters. To make it compilable, must replace
		// with
		// dots.
		retval = retval.replace('$', '.');

		return retval;
	}
}
