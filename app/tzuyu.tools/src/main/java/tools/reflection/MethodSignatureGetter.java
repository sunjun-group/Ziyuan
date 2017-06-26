/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tools.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import sav.common.core.Pair;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class MethodSignatureGetter {
	
	public static void main(String[] args) {
		List<List<String>> signs = getSignature(Thread.class, "init", "currentThread");
		for (List<String> sign : signs) {
			System.out.println(sign);
		}
	}
	
	@Test
	public void getConstructorSignature() {
		Class<?> type = Thread.class;
		for (Constructor<?> constructor : type.getConstructors()) {
			System.out.println(SignatureUtils.createMethodNameSign("[cinit]",
					SignatureUtils.getParamsSignature(constructor.getParameterTypes())));
		}
	}
	
	public static List<List<String>> getSignature(Class<?> clazz,
			String... methodNames) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (String methodName : methodNames) {
			result.add(getSignature(clazz, methodName)); 
		}
		return result;
	}

	public static List<String> getSignature(Class<?> clazz, String methodName) {
		List<String> signs = new ArrayList<String>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				signs.add(SignatureUtils.createMethodNameSign(method));
			}
		}
		return signs;
	}
	
	@Test
	public void getSignatureForMth() {
		List<Pair<?, String>> clazzMethodList = new ArrayList<Pair<?,String>>();
		clazzMethodList.add(Pair.of(HashSet.class, "add"));
		clazzMethodList.add(Pair.of(ArrayList.class, "add"));
		clazzMethodList.add(Pair.of(HashMap.class, "put"));
		for (Pair<?, String> clazzMethod : clazzMethodList) {
			Class<?> clazz = (Class<?>) clazzMethod.a;
			String str = String.format("%s.%s: %s", clazz, clazzMethod.b,
					getSignature(clazz, clazzMethod.b));
			System.out.println(str);
		}
	}
}
