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
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.utils.CollectionUtils;


/**
 * @author LLT
 *
 */
public class VarTypeResolver extends TypeVisitor {
	private static final int RESOLVE_LEVEL = 7; // to prevent stack overflow.
	private static Logger log = LoggerFactory.getLogger(VarTypeResolver.class);
	private Map<TypeVariable<?>, Type> rTypeMap; // value can be either Class, TypeVariable or ParameterizedType
	private Set<Type> visitedTypes;
	
	private ISubTypesScanner subTypeScanner;
	
	VarTypeResolver(ISubTypesScanner subTypesScanner) {
		rTypeMap = new HashMap<TypeVariable<?>, Type>();
		visitedTypes = new HashSet<Type>();
		this.subTypeScanner = subTypesScanner;
	}
	
	public Class<?> resolve(Type type) {
		return resolve(type, 0);
	}
	
	public Class<?>[] resolve(Type[] bounds) {
		return resolve(bounds, 0);
	}
	
	private Class<?> resolve(Type type, int level) {
		if (level > RESOLVE_LEVEL) {
			return null;
		}
		TypeEnum typeEnum = TypeEnum.of(type);
		switch (typeEnum) {
		case CLASS:
			return (Class<?>) type;
		case TYPE_VARIABLE:
			return resolveTypeVariable((TypeVariable<?>) type, level + 1);
		case PARAMETER_TYPE:
			return resolveParameterType(type);
		case WILDCARDS_TYPE:
			return resolveWildcardsType((WildcardType) type, level + 1);
		case GENERIC_ARRAY_TYPE:
			// todo?
		}
		log.debug("VarTypeResolver: missing handle for the case type=", typeEnum);
		return null;
	}

	private Class<?> resolveParameterType(Type type) {
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<?>) paramType.getRawType();
	}
	
	private Class<?> resolveWildcardsType(WildcardType type, int level) {
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
			return selectResolveTypeByUpperbounds(upperBounds, level);
		}
		
		return Object.class;
	}

	private Class<?> resolveTypeVariable(TypeVariable<?> variable, int level) {
		Type mappedType = rTypeMap.get(variable);
		if (mappedType == null) {
			// assign a type for it
			Class<?> resolvedType = selectResolveTypeByUpperbounds(variable.getBounds(), level);
			putToRTypeMap(variable, resolvedType);
			return resolvedType;
		}
		Class<?> resolvedType = resolve(mappedType, level + 1);
		if ((TypeEnum.isClass(mappedType) && resolvedType != mappedType)) {
			putToRTypeMap(variable, resolvedType);
		}
		return resolvedType;
	}

	private Class<?> selectResolveTypeByUpperbounds(Type[] bounds, int level) {
//		 if (CollectionUtils.isEmpty(bounds)) {
//			 return resolve(Object.class, level);
//		 } 
//		 Class<?>[] implClasses = resolve(bounds, level);
//		 return subTypeScanner.getRandomImplClzz(implClasses);
		Class<?>[] implClazz = resolve(bounds, level);
		return subTypeScanner.getRandomImplClzz(implClazz);
	}
	
	private Class<?>[] resolve(Type[] bounds, int level) {
		Class<?>[] resolvedTypes = new Class<?>[bounds.length];
		for (int i = 0; i < bounds.length; i++) {
			resolvedTypes[i] = resolve(bounds[i], level);
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
		// check state
		if (clazz.getTypeParameters().length != paramTypes.length) {
			log.error(getClass().getSimpleName(), "paramTypes and class variables mismatch, paramTypes=",
					paramTypes, "class=", clazz);
			throw new IllegalArgumentException("paramTypes and class variables mismatch!!");
		}
		TypeVariable<?>[] vars = clazz.getTypeParameters();
		Map<TypeVariable<?>, Type> assignTypes = new HashMap<TypeVariable<?>, Type>();
		for (int i = 0; i < paramTypes.length; i++) {
			assignTypes.put(vars[i], paramTypes[i]);
		}
		importTypeMap(assignTypes);
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
	}

	@Override
	protected void visit(ParameterizedType type) {
		Class<?> rawClass = (Class<?>) type.getRawType();
		TypeVariable<?>[] vars = rawClass.getTypeParameters();
		Type[] typeArgs = type.getActualTypeArguments();
		for (int i = 0; i < vars.length; i++) {
			Type argType = typeArgs[i];
//			log.debug("visit parameterized type-argType={}", argType);
			putToRTypeMap(vars[i], argType);
		}
		visit(rawClass);
	}
	
	private void putToRTypeMap(TypeVariable<?> var, Type argType) {
		if (var == null || argType == null) {
			return;
		}
		if (!rTypeMap.containsKey(var)) {
			if (var.equals(argType)) {
				log.warn("duplicate var: type {} is not solved!", var);
			} else {
				rTypeMap.put(var, argType);
			}
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
	
	Map<TypeVariable<?>, Type> getrTypeMap() {
		return ResolverTypeMap.of(rTypeMap);
	}

	/**
	 * only import if the value for TypeVariable is not defined yet.
	 */
	public void importTypeMap(Map<TypeVariable<?>, Type> typeMap) {
		for (Entry<TypeVariable<?>, Type> entry : typeMap.entrySet()) {
			TypeVariable<?> lastType = entry.getKey();
			Type mappedType = lastType;
			while (TypeEnum.isTypeVariable(mappedType)) {
				lastType = (TypeVariable<?>) mappedType;
				mappedType = rTypeMap.get(lastType);
			}
			if (mappedType == null) {
				putToRTypeMap(lastType, entry.getValue());
			}
		}
	}
}
