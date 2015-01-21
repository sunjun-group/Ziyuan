/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.service.impl;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
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

	protected Set<Class<?>> filterAndSelect(Set<?> subTypes, Class<?> type) {
		Set<Class<?>> selectedSubTypes = new HashSet<Class<?>>();
		String typePkg = type.getPackage().getName();
		for (Object obj : subTypes) {
			Class<?> subType = (Class<?>) obj;
			int modifiers = subType.getModifiers();
			/* subType must be accessible */
			if (Modifier.isPublic(modifiers)) {
				/**/
				if (typePkg.equals(subType.getPackage().getName())) {
					selectedSubTypes.add(subType);
				}
			}
		}
		return selectedSubTypes;
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
}
