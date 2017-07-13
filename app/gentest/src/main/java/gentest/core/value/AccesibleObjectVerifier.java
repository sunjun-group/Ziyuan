/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value;

import java.lang.reflect.AccessibleObject;

/**
 * @author LLT
 *
 */
public class AccesibleObjectVerifier {
	private AccesibleObjectVerifier(){}
	
	private static boolean isAccessRestrictionClass(Class<?> paramType) {
		return "sun.util.locale.BaseLocale".equals(paramType.getName());
	}

	public static boolean verify(AccessibleObject constructorOrMethod, Class<?>[] parameterTypes) {
		for (Class<?> paramType : parameterTypes) {
			if (isAccessRestrictionClass(paramType)) {
				return false;
			}
		}
		return true;
	}
}
