/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.adapter;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameRequestor;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import tzuyu.engine.model.exception.TzException;
import tzuyu.engine.utils.Pair;
import tzuyu.engine.utils.Randomness;
import tzuyu.plugin.commons.exception.PluginException;
import tzuyu.plugin.commons.utils.ClassLoaderUtils;
import tzuyu.plugin.commons.utils.IResourceUtils;
import tzuyu.plugin.tester.command.gentest.GenTestPreferences;
import tzuyu.plugin.tester.preferences.SearchScope;
import tzuyu.plugin.tester.preferences.TypeScope;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public class PluginReferencesAnalyzer {
	// add this into the configuration
	private int numOfRandomClzzToCache = 10;
	private int MAX_TO_TRY = 3;
	private IJavaProject project;
	private Map<Class<?>, List<String>> classMapCache;
	private Map<String, TypeScope> typeScopeMap;
	private URLClassLoader classLoader;
	private GenTestPreferences config;

	public PluginReferencesAnalyzer(IJavaProject project,
			GenTestPreferences config) throws PluginException {
		classMapCache = new HashMap<Class<?>, List<String>>();
		this.project = project;
		this.typeScopeMap = config.getSearchScopeMap();
		classLoader = ClassLoaderUtils.getClassLoader(project);
		this.config = config;
	}
	
	@Override
	public Class<?> getRandomImplClzz(Class<?> clazz) {
		Class<?> result = null;
		try {
			// find in type scope first, if user_defined => randomly pick up a class
			// in defined implementation list
			SearchScope searchScope = SearchScope.SOURCE_JARS;
			TypeScope typeScope = typeScopeMap.get(clazz.getName());
			if (typeScope != null) {
				searchScope = typeScope.getScope();
			}
			if (searchScope == SearchScope.USER_DEFINED) {
				Pair<String, IType> implType = Randomness
						.randomMember(typeScope.getImplTypes());
				return toClass(implType.a);
			}
			/*
			 * for search scope = (SOURCE/SOURCE_JARS), we need to pick up an
			 * implementation class in project randomly
			 */
			for (int i = 0; i < MAX_TO_TRY && result == null; i++) {
				if (clazz == Class.class || clazz == Object.class) {
					result = getRandomClass(searchScope);
				} else if (clazz == Enum.class) {
					result = getRandomEnum(searchScope);
				} else {
					result = getRandomImplForSpecificType(clazz,
							searchScope);
				}
			}
		} catch (TzException e) {
			PluginLogger.getLogger().logEx(e);
		}
		return result;
	}
	
	/**
	 * this function is used for searching a certain class for type Class<?> or
	 * Object
	 */
	public Class<?> getRandomClass(SearchScope searchScope) {
		Class<?> result = null;
		// search the cache first
		List<String> mappedList = classMapCache.get(Class.class);
		if (CollectionUtils.isEmpty(mappedList)) {
			mappedList = new ArrayList<String>();
			classMapCache.put(Class.class, mappedList);
		}
		try {
			String mappedClz = null;
			if (mappedList.size() < numOfRandomClzzToCache) {
				IType type = getRandomClassFromProject(project, searchScope);
				if (type != null && !type.isInterface()) {
					mappedClz = toClassName(type);
					mappedList.add(mappedClz);
				}
			} else {
				mappedClz = Randomness.randomMember(mappedList);
			}
			result = toClass(mappedClz);
		} catch (Exception e) {
			// do nothing
		}
		return result;
	}
	
	/**
	 * load a class by fullyQualifiedName
	 */
	private Class<?> toClass(String mappedClz) throws TzException {
		try {
			if (mappedClz == null) {
				return null;
			}
			return classLoader.loadClass(mappedClz);
		} catch (ClassNotFoundException e) {
			PluginLogger.getLogger().logEx(e);
		}
		return null;
	}

	private String toClassName(IType type) {
		return type.getFullyQualifiedName();
	}
	
	/**
	 * extract fullyQualifiedNames of types
	 */
	private List<String> toClassName(List<IType> types) {
		List<String> result = new ArrayList<String>();
		for (IType type : types) {
			result.add(toClassName(type));
		}
		return result;
	}

	
	public Class<?> getRandomEnum(SearchScope searchScope) {
		List<String> enums = classMapCache.get(Enum.class);
		if (CollectionUtils.isEmpty(enums)) {
			enums = new ArrayList<String>();
			classMapCache.put(Enum.class, enums);
			try {
				List<String> enumTypes = performSearchEnums(project,
						numOfRandomClzzToCache, searchScope);
				for (String eString : enumTypes) {
					CollectionUtils.addIfNotNull(enums, 
							toClassName(project.findType(eString)));
				}
			} catch (CoreException e) {
				PluginLogger.getLogger().logEx(e);
			}
		}
		try {
			return toClass(Randomness.randomMember(enums));
		} catch (TzException e) {
			return null;
		}
	}

	/**
	 * using eclipse API to search enums of the project
	 * limited by the limit number.
	 * Because eclipse perform the searching on the project source first, 
	 * then on the binary sources.
	 * So, we should limit the size of result list for the performance. 
	 */
	private List<String> performSearchEnums(IJavaProject proj, final int limit,
			SearchScope searchScope) {
		final List<String> result = new ArrayList<String>();
		IJavaSearchScope scope = searchScope.getIJavaSearchScope(proj);
		
		// for cancel the the search perform when we found enough enum for test.
		final IProgressMonitor progressMonitor = new NullProgressMonitor();  
		TypeNameRequestor nameRequestor = new TypeNameRequestor() {
			@Override
			public void acceptType(int modifiers, char[] packageName,
					char[] simpleTypeName, char[][] enclosingTypeNames,
					String path) {
				if (Flags.isPublic(modifiers)) {
					result.add(StringUtils.dotJoinStr(packageName,
							enclosingTypeNames, simpleTypeName));
				}
				if (result.size() == limit) {
					progressMonitor.setCanceled(true);
				}
			}
		};
		int matchRule = SearchPattern.R_PREFIX_MATCH;
		SearchEngine searchEngine = new SearchEngine();
		try {
			searchEngine.searchAllTypeNames(new char[]{}, matchRule, new char[]{},
					matchRule, IJavaSearchConstants.ENUM, scope, nameRequestor, 
					IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
					progressMonitor);
			return result;
		} catch (OperationCanceledException e) {
			// do nothing, just return.
			return result;
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			return new ArrayList<String>();
		}
	}

	/**
	 * currently, this function only support for interface and 
	 * abstract class (includes Enum<?>).
	 */
	private Class<?> getRandomImplForSpecificType(Class<?> clazz,
			SearchScope searchScope) {
		try {
			if (!clazz.isInterface() && Modifier.isAbstract(clazz.getModifiers())) {
				return clazz;
			}
			// for interface, using eclipse API to get its implemetation.
			List<String> availableSubTypes = classMapCache.get(clazz);
			if (availableSubTypes == null) {
				IType type = IResourceUtils.getIType(project, clazz);
				IType[] allSubtypes;
				try {
					allSubtypes = getSubtypesByScope(project, type, searchScope);
					availableSubTypes = toClassName(getFilteredRandomSubList(
							Arrays.asList(allSubtypes), numOfRandomClzzToCache));
				} catch (JavaModelException e) {
					PluginLogger.getLogger().logEx(e);
				}
				classMapCache.put(clazz, availableSubTypes);
			}

			String subType = Randomness.randomMember(availableSubTypes);
			PluginLogger.getLogger().debugGetImplForType(clazz, subType);
			return toClass(subType);
		} catch (TzException e) {
			return null;
		}
	}

	private IType getRandomClassFromProject(IJavaProject proj, SearchScope searchScope) {
		try {
			List<IPackageFragment> filteredPkgs = IResourceUtils
					.filterSourcePkgs(proj.getPackageFragments(), searchScope);
			filteredPkgs.remove(config.getPassPackage());
			filteredPkgs.remove(config.getFailPackage());
			IPackageFragment pkg = Randomness.randomMember(filteredPkgs);
			ICompilationUnit cu = Randomness.randomMember(pkg
					.getCompilationUnits());
			if (cu == null) {
				return null;
			}
			for (IType type : cu.getTypes()) {
				if (Flags.isPublic(type.getFlags())) {
					return type;
				}
			}
			return null;
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			return null;
		}
	}

	private List<IType> getFilteredRandomSubList(List<IType> allList,
			int sublistMaxSize) {
		if (CollectionUtils.isEmpty(allList)) {
			return new ArrayList<IType>();
		}
		int size = sublistMaxSize;
		if (size >= allList.size()) {
			size = allList.size();
		}
		List<IType> result = new ArrayList<IType>();
		Collections.shuffle(allList);
		int i = 0;
		for (Iterator<IType> it = allList.iterator(); it.hasNext() && i < size;) {
			IType iType = it.next();
			try {
				int flags = iType.getFlags();
				if (!Flags.isAbstract(flags) && Flags.isPublic(flags)
						&& !Flags.isInterface(flags)) {
					result.add(iType);
					i++;
				}
			} catch (JavaModelException e) {
				PluginLogger.getLogger().logEx(e);
			}

		}
		return result;
	}
	
	public static IType[] getSubtypesByScope(IJavaProject project, IType type,
			SearchScope scope) throws JavaModelException {
		IType[] allSubtypes = getAllSubtypes(project, type);
		if (scope == SearchScope.SOURCE_JARS) {
			return allSubtypes;
		}
		List<IType> types = new ArrayList<IType>();
		for (IType subType : allSubtypes) {
			if (!subType.isBinary()) {
				types.add(subType);
			}
		}
		return types.toArray(new IType[types.size()]);
	}
	
	/**
	 * get all subtypes for an interface or abstract classes
	 */
	public static IType[] getAllSubtypes(IJavaProject project, IType type)
			throws JavaModelException {
		ITypeHierarchy typeHierachy = type.newTypeHierarchy(project, null);
		if (type.isInterface()) {
			return typeHierachy.getImplementingClasses(type);
		}
		return typeHierachy.getSubclasses(type);
	}

	/* (non-Javadoc)
	 * @see sav.strategies.gentest.ISubTypesScanner#getRandomImplClzz(java.lang.Class<?>[])
	 */
	@Override
	public Class<?> getRandomImplClzz(Class<?>[] bounds) {
		// TODO Auto-generated method stub
		return null;
	}
}
