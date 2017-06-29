/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import gentest.core.data.MethodCall;
import gentest.core.data.typeinitilizer.TypeInitializer;
import gentest.core.value.store.iface.ITypeInitializerStore;
import gentest.injection.TestcaseGenerationScope;

/**
 * @author LLT
 *
 */
@TestcaseGenerationScope
public class TypeInitializerStore implements ITypeInitializerStore {
	private static Logger log = LoggerFactory.getLogger(TypeInitializerStore.class);
	private LoadingCache<String, TypeInitializer> typeInitializerCache;
	@Inject @Named("prjClassLoader")
	private ClassLoader prjClassLoader;
	
	public TypeInitializerStore() {
		typeInitializerCache =  CacheBuilder.newBuilder().build(new CacheLoader<String, TypeInitializer>() {
			@Override
			public TypeInitializer load(String className) throws Exception {
				Class<?> type = prjClassLoader.loadClass(className);
				if (type == null) {
					log.error("cannot load class: {}", className);
					return new TypeInitializer(type);
				}
				TypeInitializer initializer = InitialializerByDefaultLoader.load(type);
				if (initializer == null) {
					initializer = initTypeInitializer(type);
				}
				return initializer;
			}
		});
	}
	
	@Override
	public TypeInitializer load(Class<?> type) {
		if (type == null) {
			return null;
		}
		try {
			return typeInitializerCache.get(type.getName());
		} catch (ExecutionException e) {
			log.debug("error when executing cache to get constructor for {}: {}", type.getName(), e.getMessage());
			return null;
		}
	}

	//----------------init typeInitializer--------------------
	/**
	 * return 
	 * constructor: if the class has it own visible constructor 
	 * 			or if not, the visible constructor of extended class will be returned
	 * method: means static method, if the class does not have visible constructor but static initialization method
	 * methodCall: if the class has a builder inside. 
	 */
	public static TypeInitializer initTypeInitializer(Class<?> type) {
		TypeInitializer initializer = new TypeInitializer(type);
		List<Object> badConstructors = new ArrayList<Object>();
		try {
			/*
			 * try with the perfect one which is public constructor with no
			 * parameter
			 */
			Constructor<?> constructor = type.getConstructor();
			if (canBeCandidateForConstructor(constructor, type, badConstructors)) {
				initializer.addConstructor(constructor, false);
			}
		} catch (Exception e) {
			// do nothing, just keep trying.
		}
		for (Constructor<?> constructor : type.getConstructors()) {
			if (canBeCandidateForConstructor(constructor, type, badConstructors)) {
				initializer.addConstructor(constructor, true);
			}
		}
		
		/* try to find static method for initialization inside class */
		for (Method method : type.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())) {
				if (method.getReturnType().equals(type)
						&& canBeCandidateForConstructor(method, type, badConstructors)) {
					initializer.addStaticMethod(method);
				}
			}
		}
		
		/* try to find a builder inside class */
		Class<?>[] declaredClasses = type.getDeclaredClasses();
		if (declaredClasses != null) {
			for (Class<?> innerClazz : declaredClasses) {
				for (Method method : innerClazz.getMethods()) {
					if (method.getReturnType().equals(type)) {
						initializer.addBuilderMethodCall(MethodCall.of(method, innerClazz));
					}
				}
			}
		}
		
		if (initializer.hasNoConstructor() && !badConstructors.isEmpty()) {
			initializer.addBadConstructors(badConstructors);
		}
		return initializer;
	}

	private static boolean canBeCandidateForConstructor(Constructor<?> constructor,
			Class<?> type, List<Object> badConstructors) {
		return canBeCandidateForConstructor(constructor, constructor.getParameterTypes(), constructor.getModifiers(),
				type, badConstructors);
	}
	
	private static boolean canBeCandidateForConstructor(Method staticMethod,
			Class<?> type, List<Object> badConstructors) {
		return canBeCandidateForConstructor(staticMethod, staticMethod.getParameterTypes(), staticMethod.getModifiers(),
				type, badConstructors);
	}
	
	private static boolean canBeCandidateForConstructor(AccessibleObject constructorOrMethod,
			Class<?>[] parameterTypes, int modifier,
			Class<?> type, List<Object> badConstructors) {
		for (Class<?> paramType : parameterTypes) {
			/* loop */
			if (type.equals(paramType) || paramType.isAssignableFrom(type)) {
				return false;
			}
		}
		boolean isPublic = Modifier.isPublic(modifier);
		/* deprecated methods/constructor */
		if (isPublic && constructorOrMethod.isAnnotationPresent(Deprecated.class)) {
			badConstructors.add(constructorOrMethod);
			return false;
		}
		return isPublic;
	}

	//------------------------------------------------------------------------------------------

	public void setPrjClassLoader(ClassLoader prjClassLoader) {
		this.prjClassLoader = prjClassLoader;
	}
}
