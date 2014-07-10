/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.commons.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import analyzer.SignatureUtils;

import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.plugin.commons.exception.PluginException;

/**
 * @author LLT
 *
 */
public class MethodUtils {
	
	public static IMethod findMethod(IType type, Method method) {
		List<IMethod> potentialMatchedMethods = new ArrayList<IMethod>();
		try {
			for (IMethod jmethod : type.getMethods()) {
				if (jmethod.getElementName().equals(method.getName())
						&& jmethod.getParameterTypes().length == method.getParameterTypes().length) {
					potentialMatchedMethods.add(jmethod);
				}
			}
			if (potentialMatchedMethods.size() == 1) {
				return potentialMatchedMethods.get(0);
			}
			SignatureParser parser = new SignatureParser(type);
			String methodSign = SignatureUtils.getSignature(method);
			for (IMethod jmethod : potentialMatchedMethods) {
				if (methodSign.equals(parser.toMethodJVMSignature(
								jmethod.getParameterTypes(),
								jmethod.getReturnType()))) {
					return jmethod;
				}
			}
			throw new TzRuntimeException(getErrorMessage(type, method,
					methodSign));
		} catch (JavaModelException e) {
			throw new TzRuntimeException(getErrorMessage(type, method,
					SignatureUtils.getSignature(method)));
		} catch (PluginException e) {
			throw new TzRuntimeException(getErrorMessage(type, method,
					SignatureUtils.getSignature(method)));
		}
		
	}

	private static String getErrorMessage(IType type, Method method,
			String methodSign) {
		return String.format("cannot find method %s(%s) in type %s", 
				method.getName(), methodSign, type.getElementName());
	}
}
