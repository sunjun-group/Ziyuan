/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

import java.lang.reflect.Method;

import org.junit.Test;

import testdata.type.paramtype.VariableClass;

/**
 * @author LLT
 *
 */
public class SignatureUtilsTest {

	@Test
	public void testParamsMethod() {
		Method method = ClassUtils.loockupMethod(VariableClass.class, "method");
		String signature = SignatureUtils.getSignature(method);
		System.out.println(signature);
	}
}
