/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gentest.core.data.typeinitilizer.TypeInitializer;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class InitialializerByDefaultLoader {
	private static final String DEFAULT_CONSTRUCTOR_NAME = "<clinit>";
	private static final Map<String, List<String>> DEFAULT_CONSTRUCTOR_SIGNS;
	static {
		DEFAULT_CONSTRUCTOR_SIGNS = new HashMap<String, List<String>>();
		DEFAULT_CONSTRUCTOR_SIGNS.put(Thread.class.getName(), Arrays.asList(DEFAULT_CONSTRUCTOR_NAME + "(Ljava/lang/String;)"));
	}
	private InitialializerByDefaultLoader(){}

	public static TypeInitializer load(Class<?> type) {
		List<String> constructorSigns = DEFAULT_CONSTRUCTOR_SIGNS.get(type.getName());
		if (constructorSigns == null) {
			return null;
		}
		return initTypeInitializer(type, constructorSigns);
	}

	private static TypeInitializer initTypeInitializer(Class<?> type, List<String> constructorSigns) {
		TypeInitializer initializer = new TypeInitializer(type);
		for (Constructor<?> constructor : type.getConstructors()) {
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			if (constructorSigns.contains(getConstructorSignature(parameterTypes))) {
				initializer.addConstructor(constructor, parameterTypes.length != 0);
			}
		}
		return initializer;
	}

	private static String getConstructorSignature(Class<?>[] parameterTypes) {
		return SignatureUtils.createMethodNameSign(DEFAULT_CONSTRUCTOR_NAME, SignatureUtils.getParamsSignature(parameterTypes));
	}
}
