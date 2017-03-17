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
import sav.common.core.utils.Randomness;
import sav.strategies.gentest.ISubTypesScanner;


/**
 * @author LLT
 *
 */
public class VarTypeResolver extends TypeVisitor {
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
		case GENERIC_ARRAY_TYPE:
			// todo?
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
		if ((TypeEnum.isClass(mappedType) && resolvedType != mappedType)) {
			rTypeMap.put((TypeVariable<?>) variable, resolvedType);
		}
		return resolvedType;
	}

	private Class<?> selectResolveTypeByUpperbounds(Type[] bounds) {
		 if (CollectionUtils.isEmpty(bounds)) {
			 return resolve(Object.class);
		 } 
		 Class<?>[] implClasses = resolve(bounds);
		 return Randomness.randomMember(implClasses);
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
			log.debug("visit parameterized type-argType=", argType);
			if (!rTypeMap.containsKey(vars[i])) {
				rTypeMap.put(vars[i], argType);
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
				rTypeMap.put(lastType, entry.getValue());
			}
		}
	}
}
