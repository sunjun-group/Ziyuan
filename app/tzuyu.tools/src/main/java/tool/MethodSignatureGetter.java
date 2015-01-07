/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tool;

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

	public static List<String> getSignature(Class<?> clazz, String methodName) {
		List<String> signs = new ArrayList<String>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				signs.add(SignatureUtils.getSignature(method));
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
