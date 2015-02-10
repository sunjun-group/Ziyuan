/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class VarType implements IType {
	private Class<?> rawType;
	private Type type;
	private VarTypeResolver resolver;
	private VarTypeCreator creator;

	VarType(VarTypeResolver resolver, Class<?> clazz, Class<?>... paramTypes) {
		setResolver(resolver);
		rawType = clazz;
		if (CollectionUtils.isNotEmpty(paramTypes)) {
			resolver.assignParamTypes(clazz, paramTypes);
		}
	}
	
	//TODO LLT: to remove
	public VarType(VarTypeResolver resolver, Class<?> clazz, Type type) {
		this(resolver, clazz);
		this.type = type;
	}

	@Override
	public Class<?> getRawType() {
		return rawType;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public IType resolveType(Class<?> a) {
		if (ClassUtils.isAupperB(rawType, a)) {
			return resolveSubType(a);
		}
		TypeVariable<?>[] typeParameters = a.getTypeParameters();
		if (CollectionUtils.isNotEmpty(typeParameters)) {
			Class<?>[] paramTypes = getResolver().resolve(typeParameters);
			return creator.forParamClass(a, paramTypes);
		}
		return creator.forClass(a);
	}

	public IType resolveSubType(Class<?> a) {
		VarType subtype = (VarType)creator.forClass(a);
		if (CollectionUtils.isNotEmpty(this.rawType.getTypeParameters())) {
			Class<?>[] typeParams = getResolver().resolve(
					this.rawType.getTypeParameters());
			subtype.getResolver().assignParamTypes(this.rawType, typeParams);
		}
		
		return subtype;
	}

	@Override
	public IType resolveType(Type type) {
		return creator.forType(type, resolver);
	}
	
	@Override
	public IType[] resolveType(Type[] genericParameterTypes) {
		IType[] types = new IType[genericParameterTypes.length];
		for (int i = 0; i < genericParameterTypes.length; i++) {
			types[i] = resolveType(genericParameterTypes[i]);
		}
		return types;
	}

	
	private VarTypeResolver getResolver() {
		resolver.visitType(rawType);
		return resolver;
	}
	
	public void setResolver(VarTypeResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public String toString() {
		return "VarType [rawType=" + rawType + "]";
	}

	public void setCreator(VarTypeCreator creator) {
		this.creator = creator;
	}

	public boolean isArray() {
		return false;
	}

	@Override
	public IType getComponentType() {
		return null;
	}
}
