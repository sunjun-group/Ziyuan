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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class VarType implements IType {
	private Class<?> rawType;
	private Type genericType;
	private VarTypeResolver resolver;
	private VarTypeCreator creator;
	
	VarType(VarTypeResolver resolver, Class<?> clazz, Class<?>... paramTypes) {
		setResolver(resolver);
		rawType = clazz;
		resolver.visitType(rawType);
		if (CollectionUtils.isNotEmpty(paramTypes)) {
			resolver.assignParamTypes(clazz, paramTypes);
		}
	}
	
	@Override
	public Class<?> getRawType() {
		return rawType;
	}

	@Override
	public Type getType() {
		if (genericType == null) {
			TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
			if (CollectionUtils.isEmpty(typeParameters)) {
				return rawType;
			}
			Map<TypeVariable<?>, Type> typeMap = new HashMap<TypeVariable<?>, Type>();
			for (TypeVariable<?> paramType : typeParameters) {
				typeMap.put(paramType, getResolver().resolve(paramType));
			}
			genericType = TypeUtils.parameterize(rawType, typeMap);
		}
		return genericType; 
		
	}

	@Override
	public IType resolveType(Class<?> a) {
		return creator.forType(a, rawType, getResolver());
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
		return creator.forType(type, rawType, getResolver());
	}
	
	@Override
	public IType[] resolveType(Type[] genericParameterTypes) {
		IType[] types = new IType[genericParameterTypes.length];
		for (int i = 0; i < genericParameterTypes.length; i++) {
			types[i] = resolveType(genericParameterTypes[i]);
		}
		return types;
	}

	protected VarTypeResolver getResolver() {
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
