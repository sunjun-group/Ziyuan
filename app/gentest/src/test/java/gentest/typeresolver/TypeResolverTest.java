/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.typeresolver;

import gentest.AbstractGTTest;
import gentest.core.data.type.IType;
import gentest.core.data.type.SubTypesScanner;
import gentest.core.data.type.VarTypeCreator;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 * 
 */
public class TypeResolverTest extends AbstractGTTest {
	private VarTypeCreator creator;
	
	@Before
	public void beforeClass() {
		creator = new VarTypeCreator();
		creator.setSubTypesScanner(new SubTypesScanner());
	}
	
	@Test
	public void resolveType_genericArray() throws Exception {
		Method method = getMethodByName(Test1.class, "staticMethodWithGenericArrayParam");
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType[] itypes = creator.forType(genericParameterTypes);
		System.out.println(Arrays.toString(itypes));
		
		IType type = creator.forParamClass(Test1.class, String.class);
		IType paramType = type.resolveType(genericParameterTypes)[0];
		System.out.println(paramType);
	}
	
	@Test
	public void getArrayClass() throws ClassNotFoundException {
		System.out.println(int.class.getName());
		String className = SignatureUtils.getSignature(Test1[][].class)
				.replace('/', '.');
		System.out.println(className);
		System.out.println(Class.forName(className));
	}
	
	@Test
	public void resolveType_MethodArrayParam() throws Exception {
		Method method = getMethodByName(Test1.class, "methodWithArrayParams");
		IType type = creator.forParamClass(Test1.class, String.class);
		IType paramType = type.resolveType(method.getGenericParameterTypes())[0];
		System.out.println(paramType);
		Assert.assertEquals(String.class, paramType.getComponentType().getComponentType().getRawType());
		System.out.println(paramType.getRawType());
	}
	
	@Test
	public void resolveType_wildcardParam() throws Exception {
		Method method = getMethodByName(Test1.class, "wildcardMethod");
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType[] paramType = creator.forType(genericParameterTypes);
		System.out.println(Arrays.toString(paramType));
	}
	
	@Test
	public void resolveType_missingInfoClass() throws Exception {
		Method method = getMethodByName(Test1.class, "missingInfoClassMethod");
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType[] paramType = creator.forType(genericParameterTypes);
		System.out.println(Arrays.toString(paramType));
	}
	
	@Test
	public void resolveType_wildcardParam2() {
		Method method = getMethodByName(Test1.class, "staticMethodWithTest1Param");
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType[] paramType = creator.forType(genericParameterTypes);
		System.out.println(Arrays.toString(paramType));
	}

	private Method getMethodByName(Class<?> clazz, String methodName) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("cannot find method " + methodName
				+ " in class " + clazz.getName());
	}
	
	
	@Test
	public void resolveSubType() throws Exception {
		IType type = creator.forParamClass(List.class, String.class);
		IType subtype = type.resolveType(ArrayList.class);
		IType paramType = subtype.resolveType(ArrayList.class
				.getTypeParameters()[0]);
		Assert.assertEquals(String.class, paramType.getRawType());
	}

	@Test
	public void resolveTypeParam_VarTypeOfSuper_Resolved() throws Exception {
		IType type = creator.forParamClass(ArrayList.class, String.class);
		Method addMethod = List.class.getMethod("add", Object.class);
		IType[] itypes = type.resolveType(addMethod.getGenericParameterTypes());
		System.out.println(Arrays.toString(itypes));
		Assert.assertTrue(itypes.length == 1);
		Assert.assertEquals(String.class, itypes[0].getRawType());
	}
	
	@Test
	public void resolveType_inheritVarType_copyFromSubClass() throws Exception {
		IType type = creator.forParamClass(ArrayList.class, String.class);
		type = type.resolveType(List.class);
		Method addMethod = List.class.getMethod("add", Object.class);
		IType[] itypes = type.resolveType(addMethod.getGenericParameterTypes());
		System.out.println(Arrays.toString(itypes));
		Assert.assertTrue(itypes.length == 1);
		Assert.assertEquals(String.class, itypes[0].getRawType());
	}
	
	@Test
	public void resolveType_inheritVarType() throws Exception {
		IType type = creator.forParamClass(Test1.class, String.class);
		Method method = Test1.class.getMethod("mMapParam", Map.class);
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType mapType = type.resolveType(genericParameterTypes)[0];
		IType hashMapType = mapType.resolveType(HashMap.class);
		IType list = hashMapType.resolveType(List.class);
		Method addMethod = List.class.getMethod("add", Object.class);
		IType[] itypes = list.resolveType(addMethod.getGenericParameterTypes());
		System.out.println(Arrays.toString(itypes));
		Assert.assertTrue(itypes.length == 1);
		Assert.assertEquals(String.class, itypes[0].getRawType());
	}
	
	@Test
	public void resolveType_inheriVarType2() throws Exception {
		IType type = creator.forParamClass(Test1.class, String.class);
		Method method = Test1.class.getMethod("mListListParam", List.class);
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType listlistType = type.resolveType(genericParameterTypes)[0];
		Method addMethod = List.class.getMethod("add", Object.class);
		IType addParam = listlistType.resolveType(addMethod.getGenericParameterTypes())[0];
		Assert.assertEquals(List.class, addParam.getRawType());
		addParam = addParam.resolveType(addMethod.getGenericParameterTypes())[0];
		Assert.assertEquals(Integer.class, addParam.getRawType());
	}
	
	@Test
	public void resolveType_inheriVarType3() throws Exception {
		Method method = Test1.class.getMethod("mListParam", List.class);
		IType listType = creator.forType(method.getGenericParameterTypes())[0];
		Method addMethod = List.class.getMethod("add", Object.class);
		IType addParamType = listType.resolveType(addMethod.getGenericParameterTypes())[0];
		Assert.assertEquals(Integer.class, addParamType.getRawType());
	}
	
	@Test
	public void varTypeResolveType_methodWithClassParam() throws Exception {
		IType type = creator.forParamClass(Test1.class, String.class);
		Method method = Test1.class.getMethod("methodWithClassParam", Object.class);
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType[] itypes = type.resolveType(genericParameterTypes);
		System.out.println(Arrays.toString(itypes));
	}
	
	@Test
	public void varTypeResolveType_methodWithParams() throws Exception {
		IType type = creator.forParamClass(Test1.class, String.class);
		Method method = Test1.class.getMethod("methodWithParams", Object.class, Object.class);
		IType[] itypes = type.resolveType(method.getGenericParameterTypes());
		System.out.println(Arrays.toString(itypes));
	}
	
	@Test
	public void varTypeResolveType_staticMethodWithObjectParam() throws Exception {
		Method method = Test1.class.getMethod("staticMethodWithObjectParam", Object.class);
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		IType[] itypes = creator.forType(genericParameterTypes);
		System.out.println(Arrays.toString(itypes));
	}
	
	@Test
	public void varTypeResolveType_staticMethodWithParam() throws Exception {
		Method method = Test1.class.getMethod("staticMethodWithParam", IParamType.class);
		IType[] itypes = creator.forType(method.getGenericParameterTypes());
		System.out.println(Arrays.toString(itypes));
	}
	
	@SuppressWarnings("unused")
	private static class Test1<P> {
		
		public void methodWithClassParam(P param) {
			
		}
		
		public <T> void methodWithParams(P param, T anotherParam) {
			
		}
		
		public <T> void methodWithArrayParams(P[][] param, T[] anotherParam) {
			
		}
		
		public static <T extends IParamType> void staticMethodWithParam(T param) {
			
		}
		
		public static <T> void staticMethodWithObjectParam(T param) {
			
		}
		
		public static <T> void staticMethodWithGenericArrayParam(T[] param) {
			
		}
		
		public static <T> void staticMethodWithTest1Param(Test1<T> param) {
			
		}
		
		public static void wildcardMethod(Test1<? extends AbstractList<?>> test1) {
			
		}
		
		public static void missingInfoClassMethod(Test1 test1) {
			
		}
		
		public static void mMapParam(Map<Integer, List<String>> map) {
			
		}
		
		public static void mListListParam(List<List<Integer>> list) {
			
		}
		
		public static void mListParam(List<Integer> list) {
			
		}
	}
	
	private static interface IParamType {
		
	}
	
	public static class ParamType implements IParamType {
		
	}
}
