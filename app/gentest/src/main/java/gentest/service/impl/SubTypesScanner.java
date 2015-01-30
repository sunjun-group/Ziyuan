/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.service.impl;

import gentest.main.GentestConstants;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
	
	public SubTypesScanner() {
		subTypesCache = CacheBuilder.newBuilder().build(
				new CacheLoader<Class<?>, Set<Class<?>>>() {

					@Override
					public Set<Class<?>> load(Class<?> key) throws Exception {
						Reflections reflections = new Reflections(
								new ConfigurationBuilder().setUrls(Arrays
										.asList(ClasspathHelper.forClass(key))));
						Set<?> subTypes = reflections.getSubTypesOf(key);
						return filterAndSelect(subTypes, key);
					}
				});
	}

	/**
	 * from all found subTypes of a class, 
	 * filter all subTypes not public
	 * and 
	 */
	protected Set<Class<?>> filterAndSelect(Set<?> subTypes, Class<?> type) {
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
		boolean noCloseSubTypes = selectedSubTypes.isEmpty();
		for (Iterator<?> it = subTypes.iterator(); it.hasNext();) {
			if (noCloseSubTypes
					|| Randomness
							.weighedCoinFlip(GentestConstants.PROBABILITY_OF_UNCLOSED_SUBTYPES)) {
				selectedSubTypes.add((Class<?>) it.next());
			}
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
	public Class<?> getRandomImplClzz(Class<?> type) {
		try {
			Set<?> subTypes;
			subTypes = subTypesCache.get(type);
			if (CollectionUtils.isEmpty(subTypes)) {
				return null;
			}
			return (Class<?>) Randomness.randomMember(subTypes.toArray());
		} catch (ExecutionException e) {
			log.error((Object[]) e.getStackTrace());
			throw new SavRtException(ModuleEnum.TESTCASE_GENERATION,
					"error when executing cache to get subtypes");
		}
	}
	
	public void reset() {
		subTypesCache.cleanUp();
	}
}
