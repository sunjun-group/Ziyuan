/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.commons.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author LLT
 *
 */
public class MethodUtilsTest {
	
	@Test
	public void testFindMethod_signature() {
		Method addMethod = MethodUtils.findMethod(ArrayList.class, "add(Ljava/lang/Object;)Z");
		Assert.assertEquals("add", addMethod.getName());
		Assert.assertEquals("boolean", addMethod.getReturnType().getName());
		Assert.assertEquals(1, addMethod.getParameterTypes().length);
		Assert.assertEquals("java.lang.Object", addMethod.getParameterTypes()[0].getName());
	}
	

	@Test
	public void testFindMethod_methodName() {
		Method addMethod = MethodUtils.findMethod(ArrayList.class, "add");
		Assert.assertEquals("add", addMethod.getName());
		Assert.assertEquals("boolean", addMethod.getReturnType().getName());
		Assert.assertEquals(1, addMethod.getParameterTypes().length);
		Assert.assertEquals("java.lang.Object", addMethod.getParameterTypes()[0].getName());
	}
}
