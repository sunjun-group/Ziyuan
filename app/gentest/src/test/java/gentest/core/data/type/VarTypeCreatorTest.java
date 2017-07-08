/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.Test;

import gentest.core.commons.utils.MethodUtils;
import gentest.core.value.store.SubTypesScanner;
import testdata.type.paramtype.VariableClass;

/**
 * @author LLT
 *
 */
public class VarTypeCreatorTest {

	@Test
	public void testCreator() {
		VarTypeCreator creator = new VarTypeCreator();
		creator.setSubTypesScanner(new SubTypesScanner());
		IType type = creator.forClass(VariableClass.class);
		System.out.println(type.getType());
		Method method = MethodUtils.findMethod(VariableClass.class, "method");
		Parameter[] params = method.getParameters();
		IType arrType = type.resolveType(params[0].getParameterizedType());
		System.out.println(arrType.getType());
	}
}
