/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.util;

import static org.eclipse.jdt.core.Signature.ARRAY_TYPE_SIGNATURE;
import static org.eclipse.jdt.core.Signature.BASE_TYPE_SIGNATURE;
import static org.eclipse.jdt.core.Signature.CAPTURE_TYPE_SIGNATURE;
import static org.eclipse.jdt.core.Signature.CLASS_TYPE_SIGNATURE;
import static org.eclipse.jdt.core.Signature.C_DOLLAR;
import static org.eclipse.jdt.core.Signature.C_DOT;
import static org.eclipse.jdt.core.Signature.C_GENERIC_START;
import static org.eclipse.jdt.core.Signature.C_RESOLVED;
import static org.eclipse.jdt.core.Signature.C_SEMICOLON;
import static org.eclipse.jdt.core.Signature.C_TYPE_VARIABLE;
import static org.eclipse.jdt.core.Signature.C_UNRESOLVED;
import static org.eclipse.jdt.core.Signature.TYPE_VARIABLE_SIGNATURE;
import static org.eclipse.jdt.core.Signature.WILDCARD_TYPE_SIGNATURE;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class SignatureParser {
	private IType type;
	
	public SignatureParser(IType type) {
		this.type = type;
	}

	/**
	 * @exception: ErrorType.SIGNATURE_PARSER
	 * */
	public String toMethodJVMSignature(String[] paramTypes, String returnType)
			throws SavException {
		return Signature.createMethodSignature(toJVMSignature(paramTypes),
				toJVMSignature(returnType));
	}

	private String[] toJVMSignature(String[] paramTypes) throws SavException {
		String[] result = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			result[i] = toJVMSignature(paramTypes[i]);
		}
		return result;
	};

	private String toJVMSignature(String paramType) throws SavException {
		switch (Signature.getTypeSignatureKind(paramType)) {
		case ARRAY_TYPE_SIGNATURE:
			String elementType = Signature.getElementType(paramType);
			return paramType.replace(elementType, toJVMSignature(elementType));
		case CLASS_TYPE_SIGNATURE:
			return SignatureUtils.getSignature(getClassName(paramType, type));
		case TYPE_VARIABLE_SIGNATURE:
			return paramType;
		case BASE_TYPE_SIGNATURE:
			return paramType;
		case WILDCARD_TYPE_SIGNATURE:
		case CAPTURE_TYPE_SIGNATURE:
			return paramType; // TODO LLT
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Get class name without RESOLVED OR UNRESOLVED token,
	 * ignore element type.
	 * example:
	 * QString;  => String
	 * Qtest.List;  => List
	 * Qtest.List<QT>;  => List
	 * QClass<*>  => Class
	 */
	private String getClassName(String paramType, IType type) throws SavException {
		String ptype = getClassNameWithOwner(paramType, type);
		try {
			String[][] resolveType = type.resolveType(ptype);
			return toClassName(resolveType);
		} catch (JavaModelException e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e);
		}
	}

	private static String toClassName(String[][] resolveType) {
		return StringUtils.dotJoin(resolveType[0][0], 
				resolveType[0][1].replace(C_DOT, C_DOLLAR));
	}

	public static String getClassNameWithOwner(String paramType, IType type) throws SavException {
		char start = paramType.charAt(0);
		if (start == C_RESOLVED) {
			return paramType;
		}
		if (start != C_UNRESOLVED) {
			throw new IllegalArgumentException("paramType: " + paramType);
		}
		int end = paramType.length() - 1;
		char[] typeStr = paramType.toCharArray();
		int i = 0;
		while (i < typeStr.length) {
			char c = typeStr[i];
			if (c == C_GENERIC_START || (c == C_SEMICOLON)) {
				end = i;
				break;
			}
			i ++;
		}
		String ptype = paramType.substring(1, end);
		try {
			for (ITypeParameter typeParameter : type.getTypeParameters()) {
				if (typeParameter.getElementName().equals(ptype)) {
					String[][] resolveTypes = type.resolveType(typeParameter.getBounds()[0]);
					ptype = toClassName(resolveTypes);
				}
			}
		} catch (JavaModelException e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e.getMessage());
		}
		if (ptype.equals(C_TYPE_VARIABLE + "")) {
			return "java.lang.Object";
		}
		return ptype;
	}
	
	public static String getClassSimpleName(String paramType) throws SavException {
		switch (Signature.getTypeSignatureKind(paramType)) {
		case ARRAY_TYPE_SIGNATURE:
			String elementType = Signature.getElementType(paramType);
			return paramType.replace(elementType, getClassSimpleName(elementType));
		case CLASS_TYPE_SIGNATURE:
			String className = getClassNameWithOwner(paramType, null);
			return className.substring(className.lastIndexOf(".") + 1);
		case TYPE_VARIABLE_SIGNATURE:
			return paramType;
		case BASE_TYPE_SIGNATURE:
			return paramType;
		case WILDCARD_TYPE_SIGNATURE:
		case CAPTURE_TYPE_SIGNATURE:
			return paramType; // TODO LLT
		}
		throw new IllegalArgumentException("paramType: " + paramType);
	}

	public static String parse(IMethod method) throws SavException {
		try {
			String signature = getMethodSignature(method);
			return SignatureUtils.createMethodNameSign(method.getElementName(), signature);
		} catch (Exception e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e, "Cannot parse methodSignature for method: ", method);
		}
		
	}

	public static String getMethodSignature(IMethod method) throws SavException, JavaModelException {
		return new SignatureParser(method.getDeclaringType())
				.toMethodJVMSignature(method.getParameterTypes(), method.getReturnType());
	}
}
