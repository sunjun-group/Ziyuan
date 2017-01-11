/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import sav.common.core.utils.CollectionUtils;


/**
 * @author LLT
 *
 */
public abstract class TypeVisitor {
	
	public void visit(Type[] types) {
		if (CollectionUtils.isEmpty(types)) {
			return;
		}
		for (Type type : types) {
			visitType(type);
		}
	}

	/** handle for each type of Type **/
	public void visitType(Type type) {
		switch (TypeEnum.of(type)) {
		case CLASS:
			visit((Class<?>) type);
			break;
		case TYPE_VARIABLE:
			visit((TypeVariable<?>) type);
			break;
		case PARAMETER_TYPE:
			visit((ParameterizedType) type);
			break;
		case GENERIC_ARRAY_TYPE:
			visit((GenericArrayType) type);
			break;
		case WILDCARDS_TYPE:
			visit((WildcardType) type);
		}
	}
	
	private void visit(WildcardType type) {
		// implement when needed.		
	}

	protected void visit(GenericArrayType type) {
		// implement when needed.		
	}

	protected void visit(Class<?> type) {
		// implement when needed.
	}
	
	protected void visit(ParameterizedType type) {
		// implement when needed.
	}
	
	protected void visit(TypeVariable<?> type) {
		// implement when needed.
	}

	public static enum TypeEnum {
		CLASS,
		TYPE_VARIABLE, /* T */
		GENERIC_ARRAY_TYPE, /* T[] */
		PARAMETER_TYPE,  /* <T> */
		WILDCARDS_TYPE; /* <?> */

		public static TypeEnum of(Type type) {
			if (isClass(type)) {
				return CLASS;
			}
			if (isTypeVariable(type)) {
				return TYPE_VARIABLE;
			}
			if (isParameterizedType(type)) {
				return PARAMETER_TYPE;
			}
			if (isGenericArray(type)) {
				return GENERIC_ARRAY_TYPE;
			}
			if (isWildcardsType(type)) {
				return WILDCARDS_TYPE;
			}
			return null;
		}
		
		public static boolean isWildcardsType(Type type) {
			return type instanceof WildcardType;
		}

		public static boolean isGenericArray(Type type) {
			return type instanceof GenericArrayType;
		}

		public static boolean isParameterizedType(Type type) {
			return type instanceof ParameterizedType;
		}

		public static boolean isTypeVariable(Type type) {
			return type instanceof TypeVariable<?>;
		}

		public static boolean isClass(Type type) {
			return type instanceof Class<?>;
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends Type> T cast(Type type, Class<T> clazz) {
			if (clazz.isAssignableFrom(type.getClass())) {
				return (T) type;
			}
			return null;
		}
	}
	
}
