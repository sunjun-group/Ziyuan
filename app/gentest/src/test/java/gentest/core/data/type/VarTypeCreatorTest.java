/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import gentest.core.commons.utils.MethodUtils;
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
		IType iType = creator.forClass(VariableClass.class);
		ParameterizedType type  = (ParameterizedType) iType.getType();
		Type expectedType = type.getActualTypeArguments()[0];
		
		System.out.println(iType.getType());
		Method method = MethodUtils.findMethod(VariableClass.class, "method");
		IType arrType = iType.resolveType(method.getGenericParameterTypes()[0]);
		GenericArrayType actualType = (GenericArrayType) arrType.getType();
		
		Assert.assertEquals(expectedType, actualType.getGenericComponentType());
		System.out.println(actualType);
	}
	
	@Test
	public void testHashSet() {
		VarTypeCreator creator = new VarTypeCreator();
		creator.setSubTypesScanner(new SubTypesScanner());
		IType type = creator.forClass(Collection.class);
		ParameterizedType collectionType = (ParameterizedType) type.getType();
		Type expectedType = collectionType.getActualTypeArguments()[0];
		
		IType set = type.resolveType(HashSet.class);
		Method method = MethodUtils.findMethod(HashSet.class, "add");
		IType arrType = set.resolveType(method.getGenericParameterTypes()[0]);
		Type actualType = arrType.getType();
		
		Assert.assertEquals(expectedType, actualType);
		System.out.println(actualType);
	}
	
	@Test
	public void testArrayList() {
		VarTypeCreator creator = new VarTypeCreator();
		creator.setSubTypesScanner(new SubTypesScanner());
		IType type = creator.forClass(Collection.class);
		ParameterizedType collectionType = (ParameterizedType) type.getType();
		Type expectedType = collectionType.getActualTypeArguments()[0];
		
		IType set = type.resolveType(ArrayList.class);
		Method method = MethodUtils.findMethod(ArrayList.class, "add");
		IType arrType = set.resolveType(method.getGenericParameterTypes()[0]);
		Type actualType = arrType.getType();
		
		Assert.assertEquals(expectedType, actualType);
		System.out.println(actualType);
	}
}
