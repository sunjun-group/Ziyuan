/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package builder;

import gentest.data.MethodCall;
import gentest.data.Sequence;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.Logger;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
@SuppressWarnings("unchecked")
public abstract class GentestBuilder<T extends GentestBuilder<T>> {
	protected Logger<?> logger = Logger.getDefaultLogger();
	protected int numberOfTcs;
	protected Class<?> clazz;
	private boolean specificMethod = false;
	protected List<Method> testingMethods;
	
	public GentestBuilder(int numberOfTcs) {
		testingMethods = new ArrayList<Method>();
		this.numberOfTcs = numberOfTcs;
	}
	
	public T forClass(Class<?> clazz) {
		/*
		 * clean the previous class declaration,
		 * if previously,
		 * the class is entered without specific method, mean we have to add all methods of the class into testing method list.
		 * otherwise, just reset the current class
		 * and reset flat specificMethod to
		 */
		if (this.clazz != null && !specificMethod) {
			addAllMethods(testingMethods, clazz);
		}
		specificMethod = false;
		this.clazz = clazz;
		return (T) this;
	}
	
	public T method(String methodNameOrSign) {
		specificMethod = true;
		findAndAddTestingMethod(methodNameOrSign);
		return (T) this;
	}
	
	private void addAllMethods(List<Method> methodList, Class<?> targetClass) {
		for (Method method : targetClass.getMethods()) {
			CollectionUtils.addIfNotNull(methodList, verifyMethod(method));
		}
	}
	
	protected Method findAndAddTestingMethod(String methodNameOrSign) {
		Method testingMethod = findTestingMethod(clazz, methodNameOrSign);
		testingMethods.add(testingMethod);
		return testingMethod;
	}
	
	protected static Method verifyMethod(Method method) {
		if (method.isAccessible() && Modifier.isPublic(method.getModifiers())) {
			return method;
		}
		return null;
	}
	
	protected static Method findTestingMethod(Class<?> clazz, String methodNameOrSign) {
		if (clazz != null) {
			/* try to find if input is method name */
			for (Method method : clazz.getMethods()) {
				if (method.getName().equals(methodNameOrSign)) {
					return method;
				}
			}
			/* try to find if input is method signature */
			for (Method method : clazz.getMethods()) {
				if (SignatureUtils.getSignature(method).equals(methodNameOrSign)) {
					return method;
				}
			}
			/* cannot find class */
			throw new IllegalArgumentException(String.format("cannot find method %s in class %s", methodNameOrSign
					, clazz.getName()));
		}
		/* class not yet declared */
		throw new IllegalArgumentException(
				String.format(
						"The class for method %s is not set. Expect forClass() is called before method(String methodNameOrSign)",
						methodNameOrSign));
	}
	
	protected List<MethodCall> initMethodCalls() {
		List<MethodCall> methodCalls = new ArrayList<MethodCall>(
				testingMethods.size());
		for (int i = 0; i < testingMethods.size(); i++) {
			Method method = testingMethods.get(i);
			MethodCall methodCall = MethodCall.of(method);
			methodCalls.add(methodCall);
		}
		return methodCalls;
	}
	
	public abstract Pair<List<Sequence>, List<Sequence>> generate()
			throws SavException;
}
