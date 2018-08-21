/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.data.type;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import gentest.main.GentestConstants;
import sav.common.core.ModuleEnum;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;
import sav.common.core.utils.SingleTimer;

/**
 * @author LLT
 *
 */
public class SubTypesScanner implements ISubTypesScanner {
	private static SubTypesScanner instance = new SubTypesScanner();
	private static Logger log = LoggerFactory.getLogger(SubTypesScanner.class);
	private LoadingCache<Class<?>, Set<Class<?>>> subTypesCache;
	private LoadingCache<Class<?>[], Set<Class<?>>> subTypesBoundsCache;
	private ClassLoader prjClassLoader;
	
	static {
		ReflectionsHelper.registerUrlTypes();
	}
	public SubTypesScanner() {
		subTypesCache = CacheBuilder.newBuilder().build(
				new CacheLoader<Class<?>, Set<Class<?>>>() {

					@Override
					public Set<Class<?>> load(Class<?> key) throws Exception {
						return loadSubClasses(key, FilterType.CLOSED_SUBTYPES);
					}
				});
		subTypesBoundsCache = CacheBuilder.newBuilder().build(
				new CacheLoader<Class<?>[], Set<Class<?>>>() {

					@Override
					public Set<Class<?>> load(Class<?>[] key) throws Exception {
						Set<Class<?>> subClassOfFirstBound = loadSubClasses(key[0]);
						Set<Class<?>> subTypes = new HashSet<Class<?>>();
						for (Class<?> subType : subClassOfFirstBound) {
							boolean valid = true;
							for (int i = 1; i < key.length; i++) {
								if (!subType.isAssignableFrom(key[i])) {
									valid = false;
									break;
								}
							}
							if (valid) {
								subTypes.add(subType);
							}
						}
						return subTypes;
					}

				});
	}
	
	private Set<Class<?>> loadSubClasses(Class<?> key, FilterType... filters) {
		SingleTimer timer = SingleTimer.start("LoadSubClass");
		Reflections reflections = new Reflections(
				ConfigurationBuilder.build(prjClassLoader).setExpandSuperTypes(false));
		Set<?> subTypes = reflections.getSubTypesOf(key);
		log.debug("Subtypes of {}: {}", key.getSimpleName(), subTypes);
		Set<Class<?>> subClasses = filterAndSelect(subTypes, key, filters);
		log.debug(timer.getResult());
		return subClasses;
	}

	/**
	 * from all found subTypes of a class, 
	 * filter all subTypes not public
	 * and 
	 */
	@SuppressWarnings("unchecked")
	protected Set<Class<?>> filterAndSelect(Set<?> subTypes, Class<?> type, FilterType... filters) {
		Set<Class<?>> selectedSubTypes = new HashSet<Class<?>>();
		String typePkg = type.getPackage().getName();
		for (Iterator<?> it = subTypes.iterator(); it.hasNext();) {
			Class<?> subType = (Class<?>) it.next();
			int modifiers = subType.getModifiers();
			/* subType must be accessible */
			if (Modifier.isPublic(modifiers)) {
				if (areClosePkgs(subType.getPackage().getName(), typePkg)) {
					selectedSubTypes.add(subType);
					it.remove();
				}
			} else {
				it.remove();
			}
		}
		/* selectedSubTypes now contains all closed subtypes,
		 * and subtypes now contains all unclosed subtypes
		 */
		if (CollectionUtils.existIn(FilterType.CLOSED_SUBTYPES, filters)) {
			boolean noCloseSubTypes = selectedSubTypes.isEmpty();
			for (Iterator<?> it = subTypes.iterator(); it.hasNext();) {
				Class<?> subType = (Class<?>) it.next();
				if (noCloseSubTypes
						|| Randomness
						.weighedCoinFlip(GentestConstants.PROBABILITY_OF_UNCLOSED_SUBTYPES)) {
					selectedSubTypes.add(subType);
				}
			}
		} else {
			selectedSubTypes.addAll((Collection<? extends Class<?>>) subTypes);
		}
		return selectedSubTypes;
	}

	/**
	 * 2 packages are closed if they are equal or have the same parent
	 * package
	 */
	protected boolean areClosePkgs(String subTypePkg, String typePkg) {
		int i = 0;
		int minLength = Math.min(subTypePkg.length(), typePkg.length());
		while (i < minLength) {
			char si = subTypePkg.charAt(i);
			char ti = typePkg.charAt(i);
			if (si != ti) {
				return false;
			}
			/* have same parent package */
			if (si == GentestConstants.PACKAGE_SEPARATOR) {
				return true;
			}
			i++;
		}
		/* check if first fragments are equal */
		if (subTypePkg.length() == typePkg.length()) {
			return true;
		}
		if ((subTypePkg.length() > minLength && subTypePkg.charAt(i) == GentestConstants.PACKAGE_SEPARATOR)
				|| (typePkg.length() > minLength && typePkg.charAt(i) == GentestConstants.PACKAGE_SEPARATOR)) {
			return true;
		}
		return false;
	}
	
	@Override
	public Class<?> getRandomImplClzz(Class<?>[] bounds) {
		if (bounds.length == 0) {
			return getRandomImplClzz(Object.class);
		}
		if (bounds.length == 1) {
			return getRandomImplClzz(bounds[0]);
		}
		return randomFromCache(bounds, subTypesBoundsCache);
	}

	@Override
	public Class<?> getRandomImplClzz(Class<?> type) {
		Class<?> delegateClass = getDelegateClass(type);
		if (delegateClass != null) {
			return delegateClass;
		}
		return randomFromCache(type, subTypesCache);
	}

	public static Class<?> getDelegateClass(Class<?> type) {
		if (Object.class.equals(type)) {
			return Randomness
					.randomMember(GentestConstants.DELEGATING_CANDIDATES_FOR_OBJECT);
		}
		if (Number.class.equals(type)) {
			return Randomness.randomMember(GentestConstants.CANDIDATE_DELEGATES_FOR_NUMBER);
		}
		/* not abstract and not interface */
		int modifiers = type.getModifiers();
		if (((modifiers & (Modifier.INTERFACE | Modifier.ABSTRACT)) == 0)
				&& Modifier.isPublic(type.getModifiers())) {
			return type;
		}
		return null;
	}

	private <K>Class<?> randomFromCache(K key, LoadingCache<K, Set<Class<?>>> cache) {
		Set<?> subTypes = loadFromCache(key, cache);
		return (Class<?>) Randomness.randomMember(subTypes.toArray());
	}

	private <K> Set<?> loadFromCache(K key, LoadingCache<K, Set<Class<?>>> cache) {
		Set<?> subTypes = Collections.EMPTY_SET;
		try {
			subTypes = cache.get(key);
			if (CollectionUtils.isEmpty(subTypes)) {
				subTypes = Collections.EMPTY_SET;
			}
		} catch (ExecutionException e) {
			throw new SavRtException(ModuleEnum.TESTCASE_GENERATION,
					"error when executing cache to get subtypes: " + e.getMessage());
		}
		return subTypes;
	}
	
	public void clear() {
		subTypesCache.cleanUp();
	}
	
	private enum FilterType {
		CLOSED_SUBTYPES
	}

	@Override
	public Class<?> getRandomImplClzz(IType itype) {
		if (CollectionUtils.isEmpty(itype.getRawType().getTypeParameters())) {
			return getRandomImplClzz(itype.getRawType());
		}
		Set<?> subTypes = loadFromCache(itype.getRawType(), subTypesCache);
		List<Class<?>> assignableSubTypes = new ArrayList<Class<?>>(subTypes.size());
		for (Object obj : subTypes) {
			Class<?> subType = (Class<?>) obj;
			if (isAssignable(itype, subType)) {
				assignableSubTypes.add(subType);
			}
		}
		if (assignableSubTypes.isEmpty()) {
			List<?> subTypeList = new ArrayList<Object>(subTypes);
			return (Class<?>)Randomness.randomMember(subTypeList);
		}
		return Randomness.randomMember(assignableSubTypes);
	}
	
	private boolean isAssignable(IType itype, Class<?> subType) {
		for (Type genericIface : subType.getGenericInterfaces()) {
			if (genericIface instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) genericIface;
				if (itype.getRawType().equals(pType.getRawType()) && 
						matchVariableType(itype, pType.getActualTypeArguments())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean matchVariableType(IType itype, Type[] actualTypeArguments) {
		TypeVariable<?>[] typeParameters = itype.getRawType().getTypeParameters();
		for (int i = 0; i < typeParameters.length; i++) {
			IType resolvedType = itype.resolveType(typeParameters[i]);
			Type actualType = actualTypeArguments[i];
			if (actualType instanceof Class<?>) {
				Class<?> actualClazz = (Class<?>) actualType;
				if (!actualClazz.equals(resolvedType.getRawType())) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	public static SubTypesScanner getInstance() {
		return instance;
	}
	
	public ClassLoader getPrjClassLoader() {
		return prjClassLoader;
	}
	
	public void setPrjClassLoader(ClassLoader prjClassLoader) {
		this.prjClassLoader = prjClassLoader;
	}
}
