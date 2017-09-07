/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package apache.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;

import gentest.core.commons.utils.MethodUtils;
import gentest.core.data.type.SubTypesScanner;
import gentest.core.data.type.VarTypeResolver;
import sav.common.core.utils.CollectionUtils;
import sav.commons.AbstractTest;
import testdata.type.paramtype.VariableClass;

/**
 * @author LLT
 *
 */
public class TypeUtilsTest extends AbstractTest {

	@Test
	public void run() {
		Type type = getType(VariableClass.class);
		System.out.println(type);
	}

	@Test
	public void runArray() {
		Type type = getType(VariableClass.class);
		Method method = MethodUtils.findMethod(VariableClass.class, "method");
		
		Type arrType = getArrayType(type);
		System.out.println(arrType);
	}

	public Type getArrayType(Type type) {
		return TypeUtils.genericArrayType(type);
	}

	public Type getType(Class<?> rawType) {
		TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
		if (CollectionUtils.isEmpty(typeParameters)) {
			return rawType;
		}
		Map<TypeVariable<?>, Type> typeMap = new HashMap<TypeVariable<?>, Type>();

		Type[] typeArguments = new Type[typeParameters.length];
		for (int i = 0; i < typeParameters.length; i++) {
			TypeVariable<?> paramType = typeParameters[i];
			Class<?> resolvedType = getResolver().resolve(paramType);
			typeMap.put(paramType, resolvedType);
			typeArguments[i] = resolvedType;
		}
		return TypeUtils.parameterize(rawType, typeMap);
//		return Types.newParameterizedType(rawType, typeArguments);

	}

	private VarTypeResolver getResolver() {
		return new VarTypeResolver(new SubTypesScanner());
	}
}
