/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.store;

import gentest.main.GentestConstants;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import sav.common.core.Logger;
import sav.common.core.ModuleEnum;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;
import sav.strategies.gentest.ISubTypesScanner;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author LLT
 *
 */
public class SubTypesScanner implements ISubTypesScanner {
	private Logger<?> log = Logger.getDefaultLogger();
	private LoadingCache<Class<?>, Set<Class<?>>> subTypesCache;
	private LoadingCache<Class<?>[], Set<Class<?>>> subTypesBoundsCache;
	
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
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().setUrls(Arrays
						.asList(ClasspathHelper.forClass(key))));
		Set<?> subTypes = reflections.getSubTypesOf(key);
		log.debug("Subtypes of ", key.getSimpleName());
		log.debug(subTypes);
		return filterAndSelect(subTypes, key, filters);
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
		return loadFromCache(bounds, subTypesBoundsCache);
	}

	@Override
	public Class<?> getRandomImplClzz(Class<?> type) {
		if (Object.class.equals(type)) {
			return Randomness
					.randomMember(GentestConstants.DELEGATING_CANDIDATES_FOR_OBJECT);
		}
		if (Number.class.equals(type)) {
			return Randomness.randomMember(GentestConstants.CANDIDATE_DELEGATES_FOR_NUMBER);
		}
		return loadFromCache(type, subTypesCache);
	}

	private <K>Class<?> loadFromCache(K key, LoadingCache<K, Set<Class<?>>> cache) {
		try {
			Set<?> subTypes;
			subTypes = cache.get(key);
			if (CollectionUtils.isEmpty(subTypes)) {
				return null;
			}
			return (Class<?>) Randomness.randomMember(subTypes.toArray());
		} catch (Exception e) {
			log.debug("key =", key);
			log.error((Object[]) e.getStackTrace());
			throw new SavRtException(ModuleEnum.TESTCASE_GENERATION,
					"error when executing cache to get subtypes");
		}
	}
	
	public void clear() {
		subTypesCache.cleanUp();
	}
	
	private enum FilterType {
		CLOSED_SUBTYPES
	}
	
}
