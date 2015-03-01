/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sav.common.core.Logger;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.gentest.ISubTypesScanner;


/**
 * @author LLT
 *
 */
public class VarTypeResolver extends TypeVisitor {
	private Logger<?> log = Logger.getDefaultLogger();
	private Map<TypeVariable<?>, Type> rTypeMap;
	private Set<Type> visitedTypes;
	
	private ISubTypesScanner subTypeScanner;
	
	VarTypeResolver(ISubTypesScanner subTypesScanner) {
		rTypeMap = new HashMap<TypeVariable<?>, Type>();
		visitedTypes = new HashSet<Type>();
		this.subTypeScanner = subTypesScanner;
	}
	
	public Class<?> resolve(Type type) {
		TypeEnum typeEnum = TypeEnum.of(type);
		switch (typeEnum) {
		case CLASS:
			return (Class<?>) type;
		case TYPE_VARIABLE:
			return resolveTypeVariable((TypeVariable<?>) type);
		case PARAMETER_TYPE:
			ParameterizedType paramType = (ParameterizedType) type;
			return (Class<?>) paramType.getRawType();
		case WILDCARDS_TYPE:
			return resolveWildcardsType((WildcardType) type);
		}
		log.debug("VarTypeResolver: missing handle for the case type=", typeEnum);
		return null;
	}
	
	private Class<?> resolveWildcardsType(WildcardType type) {
		// TODO LLT[gentest clean up]: handle lower bound case
//		Type[] lowerBounds = type.getLowerBounds();
//		if (!CollectionUtils.isEmpty(lowerBounds)) {
//			List<Type> lowerOfBounds = new ArrayList<Type>();
//			for (Type lowerBound : lowerBounds) {
//				Class<?> resolvedBound = resolve(lowerBound);
//				Type candidateType = resolvedBound.getSuperclass();
//				
//			}
//		}
		Type[] upperBounds = type.getUpperBounds();
		if (!CollectionUtils.isEmpty(upperBounds)) {
			return selectResolveTypeByUpperbounds(upperBounds);
		}
		
		return Object.class;
	}

	private Class<?> resolveTypeVariable(TypeVariable<?> variable) {
		Type mappedType = rTypeMap.get(variable);
		if (mappedType == null) {
			// assign a type for it
			return selectResolveTypeByUpperbounds(variable.getBounds());
		}
		Class<?> resolvedType = resolve(mappedType);
		if (resolvedType != mappedType) {
			rTypeMap.put((TypeVariable<?>) variable, resolvedType);
		}
		return resolvedType;
	}

	private Class<?> selectResolveTypeByUpperbounds(Type[] bounds) {
		// if (CollectionUtils.isEmpty(bounds)) {
		// return resolve(Object.class);
		// } else {
		return subTypeScanner.getRandomImplClzz(resolve(bounds));
		// }
	}
	
	public Class<?>[] resolve(Type[] bounds) {
		Class<?>[] resolvedTypes = new Class<?>[bounds.length];
		for (int i = 0; i < bounds.length; i++) {
			resolvedTypes[i] = resolve(bounds[i]);
		}
		return resolvedTypes;
	}
	
	/**handle for each type of Type**/
	public void visitType(Type type) {
		if (isVisited(type)) {
			return;
		}
		super.visitType(type);
		visitedTypes.add(type);
	}

	private boolean isVisited(Type type) {
		return visitedTypes.contains(type);
	}
	
	public void assignParamTypes(Class<?> clazz, Class<?>[] paramTypes) {
		if (isVisited(clazz)) {
			return;
		}
		// check state
		if (clazz.getTypeParameters().length != paramTypes.length) {
			log.error(getClass().getSimpleName(), "paramTypes and class variables mismatch, paramTypes=",
					paramTypes, "class=", clazz);
			throw new IllegalArgumentException("paramTypes and class variables mismatch!!");
		}
		TypeVariable<?>[] vars = clazz.getTypeParameters();
		for (int i = 0; i < paramTypes.length; i++) {
			assignParamType(vars[i], paramTypes[i]);
			visitedTypes.add(vars[i]);
		}
	}
	
	private void assignParamType(TypeVariable<?> type, Class<?> param) {
		TypeVariable<?> paramKey = type;
		TypeVariable<?> assignedParam = paramKey;
		while (assignedParam != null) {
			Type assignedParamType = rTypeMap.get(assignedParam);
			assignedParam = null;
			if (assignedParamType != null
					&& TypeEnum.isTypeVariable(assignedParamType)) {
				assignedParam = (TypeVariable<?>) assignedParamType;
				paramKey = assignedParam;
				visitedTypes.add(paramKey);
			}
		}
		rTypeMap.put(paramKey, param);
	}

	protected void visit(Class<?> type) {
		if (!CollectionUtils.isEmpty(type.getTypeParameters())) {
			for (Type paramType : type.getTypeParameters()) {
				visitType(paramType);
			}
		}
		visit(type.getGenericInterfaces());
		Type genericSuperclass = type.getGenericSuperclass();
		if (genericSuperclass != null) {
			visitType(genericSuperclass);
		}
	}
	
	protected void visit(TypeVariable<?> variable) {
		//LLT: should not resolve type here, only resolve when resolve function is called.
//		rTypeMap.put(variable, resolveTypeVariable(variable));
	}

	@Override
	protected void visit(ParameterizedType type) {
		Class<?> rawClass = (Class<?>) type.getRawType();
		TypeVariable<?>[] vars = rawClass.getTypeParameters();
		Type[] typeArgs = type.getActualTypeArguments();
		for (int i = 0; i < vars.length; i++) {
			Type argType = typeArgs[i];
			log.debug("visit parameterized type-argType=", argType);
			rTypeMap.put(vars[i], argType);
		}
	}
	
	public void setSubTypeScanner(ISubTypesScanner subTypeScanner) {
		this.subTypeScanner = subTypeScanner;
	}
	
	@Override
	public String toString() {
		return "VarTypeResolver [rTypeMap=" + rTypeMap + ", visitedTypes="
				+ visitedTypes + "]";
	}
}
