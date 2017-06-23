/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import gentest.core.data.type.TypeVisitor.TypeEnum;
import gentest.core.data.type.VarType;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import sav.common.core.utils.ClassUtils;
import sav.strategies.gentest.ISubTypesScanner;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author LLT
 * 
 */
public class VarTypeCreator implements ITypeCreator {
	@Inject
	private ISubTypesScanner subTypesScanner;
	@Inject @Named("prjClassLoader")
	private ClassLoader prjClassLoader;

	@Override
	public IType forClass(Class<?> type) {
		return forParamClass(type);
	}
	
	public IType forParamClass(Class<?> type, Class<?>... paramTypes) {
		if (type.isArray()) {
			return new VarArrayType(prjClassLoader, forParamClass(type.getComponentType(),
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
			types[i] = forType(paramType, null, initTypeResolver());
		}
		return types;
	}
	
	public IType forType(Type type, Class<?> preClazz, VarTypeResolver resolver) {
		Type componentType = null;
		if (TypeEnum.isClass(type) && ((Class<?>)type).isArray()){
			componentType = ((Class<?>) type).getComponentType();
		} else if (TypeEnum.isGenericArray(type)) {
			componentType = ((GenericArrayType) type).getGenericComponentType();
		}
		if (componentType != null) {
			return new VarArrayType(prjClassLoader, forType(componentType, preClazz, resolver));
		} else {
			Class<?> resolvedType = resolver.resolve(type);
			VarType varType = new VarType(initTypeResolver(), resolvedType);
			decorateVarType(varType);
			extractAndImportArguments(resolvedType,
					varType.getResolver(), preClazz, resolver);
			extractAndImportArguments(type,
					varType.getResolver(), preClazz, resolver);
			return varType;
		}
	}

	private void extractAndImportArguments(Type type,
			VarTypeResolver resolver, Class<?> preClazz,
			VarTypeResolver preResolver) {
		HashMap<TypeVariable<?>, Type> extractedMap = new HashMap<TypeVariable<?>, Type>();
		extractArguments(type, preClazz, preResolver.getrTypeMap(), extractedMap);
		resolver.importTypeMap(extractedMap);
	}
	
	public static void extractArguments(Type type, Class<?> preClazz,
			Map<TypeVariable<?>, Type> preTypeMap, Map<TypeVariable<?>, Type> extractedMap) {
		if (extractedMap.containsKey(type)) {
			return;
		}
		switch (TypeEnum.of(type)) {
		case CLASS:
			if (preClazz != null) {
				TypeVariable<?>[] preTypeParameters = preClazz.getTypeParameters();
				Class<?> clazz = (Class<?>) type;
				if (ClassUtils.isAupperB(preClazz, clazz)
						&& preTypeParameters.length > 0) {
					for (TypeVariable<?> paramType : preTypeParameters) {
						extractArguments(paramType, preClazz, preTypeMap, extractedMap);
					}
				}
				for (TypeVariable<?> paramType : clazz.getTypeParameters()) {
					extractArguments(paramType, preClazz, preTypeMap, extractedMap);
				}
			}
			break;
		case TYPE_VARIABLE:
			TypeVariable<?> typeVar = (TypeVariable<?>) type;
			Type mapTypeVar = typeVar;
			while (TypeEnum.isTypeVariable(mapTypeVar)) {
				mapTypeVar = preTypeMap.get(mapTypeVar);
			}
			if (mapTypeVar != null) {
				extractedMap.put(typeVar , mapTypeVar);
				extractArguments(mapTypeVar, preClazz, preTypeMap, extractedMap);
			}
			break;
		case GENERIC_ARRAY_TYPE:
			GenericArrayType arrType = (GenericArrayType) type;
			Type componentType = arrType.getGenericComponentType();
			extractArguments(componentType, preClazz, preTypeMap, extractedMap);
			break;
		case PARAMETER_TYPE:
			ParameterizedType paramType = (ParameterizedType) type;
			Type[] actualTypeArguments = paramType.getActualTypeArguments();
			TypeVariable<?>[] typeVars = ((Class<?>) paramType.getRawType()).getTypeParameters();
			for (int i = 0; i < typeVars.length; i++) {
				/**
				 * we don't want the parameter of typeVar be override because of this case:
				 * List<List<Integer>>
				 * TODO LLT: There is a case that several Type are assigned to the same TypeVariable
				 * we need a complete solution for it
				 * [check TypeResolverTest.resolveType_inheriVarType2]. 
				 */
				if (!extractedMap.containsKey(typeVars[i])) {
					extractedMap.put(typeVars[i], actualTypeArguments[i]);
				}
				extractArguments(actualTypeArguments[i], preClazz, preTypeMap,
						extractedMap);
			}
			break;
		default:
			break;
		}
	}
	
	public static Map<TypeVariable<?>, Type> extractArguments(
			ParameterizedType paramType, VarTypeResolver resolver) {
		Map<TypeVariable<?>, Type> extractedMap = new HashMap<TypeVariable<?>, Type>();
		Type[] actualTypeArguments = paramType.getActualTypeArguments();
		TypeVariable<?>[] typeVars = ((Class<?>) paramType.getRawType()).getTypeParameters();
		for (int i = 0; i < actualTypeArguments.length; i++) {
			TypeVariable<?> typeVar = typeVars[i];
			Type actualType = actualTypeArguments[i];
			switch (TypeEnum.of(actualType)) {
			case CLASS:
				extractedMap.put(typeVar, actualType);
				break;
			case TYPE_VARIABLE:
				extractedMap.put(typeVar, resolver.resolve(actualType));
				break;
			case GENERIC_ARRAY_TYPE:
				GenericArrayType arrType = (GenericArrayType) actualType;
				Type componentType = arrType.getGenericComponentType();
				if (componentType instanceof ParameterizedType) {
					extractedMap.putAll(extractArguments((ParameterizedType) componentType, resolver));
				}
				break;
			case PARAMETER_TYPE:
				extractedMap.putAll(extractArguments((ParameterizedType) actualType, resolver));
				break;
			default:
				break;
			}
		}
		return extractedMap;
	}
	
	private VarTypeResolver initTypeResolver() {
		return new VarTypeResolver(subTypesScanner);
	}
	
	public void setSubTypesScanner(ISubTypesScanner subTypesScanner) {
		this.subTypesScanner = subTypesScanner;
	}

	/* (non-Javadoc)
	 * @see gentest.core.data.type.ITypeCreator#forType(java.lang.reflect.Type, java.lang.reflect.Type[])
	 */
	@Override
	public IType forType(Type type, Type... paramTypes) {
		// TODO Auto-generated method stub
		return null;
	}

}
