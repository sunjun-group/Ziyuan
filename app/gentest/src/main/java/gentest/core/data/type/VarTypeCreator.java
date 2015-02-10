/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import gentest.core.data.type.TypeVisitor.TypeEnum;
import gentest.core.value.store.SubTypesScanner;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.inject.Inject;

/**
 * @author LLT
 * 
 */
public class VarTypeCreator implements ITypeCreator {
	@Inject
	private SubTypesScanner subTypesScanner;
	
	@Override
	public IType forClass(Class<?> type) {
		return forParamClass(type);
	}
	
	public IType forParamClass(Class<?> type, Class<?>... paramTypes) {
		if (type.isArray()) {
			return new VarArrayType(forParamClass(type.getComponentType(),
					paramTypes));
		} else {
			VarType varType = new VarType(initTypeResolver(), type, paramTypes);
			return decorateVarType(varType);
		}
	}

	private VarType decorateVarType(VarType varType) {
		varType.setCreator(this);
		return varType;
	}
	
	@Override
	public IType[] forType(Type[] genericParameterTypes) {
		IType[] types = new IType[genericParameterTypes.length];
		for (int i = 0; i < genericParameterTypes.length; i++) {
			Type paramType = genericParameterTypes[i];
			types[i] = forType(paramType, initTypeResolver());
		}
		return types;
	}
	
	public IType forType(Type type, VarTypeResolver resolver) {
		if (TypeEnum.isParameterizedType(type)) {
			ParameterizedType paramType = (ParameterizedType) type;
			return forParamClass((Class<?>) paramType.getRawType(),
					resolver.resolve(paramType.getActualTypeArguments()));
		}
		if (TypeEnum.isGenericArray(type)) {
			Type componentType = ((GenericArrayType) type)
					.getGenericComponentType();
			return new VarArrayType(forType(componentType, resolver));
		}
		return forClass(resolver.resolve(type));
	}
	
	private VarTypeResolver initTypeResolver() {
		return new VarTypeResolver(subTypesScanner);
	}
	
	public void setSubTypesScanner(SubTypesScanner subTypesScanner) {
		this.subTypesScanner = subTypesScanner;
	}

}
