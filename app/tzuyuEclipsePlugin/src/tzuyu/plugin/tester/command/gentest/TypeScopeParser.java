/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.command.gentest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import sav.common.core.utils.Assert;
import sav.common.core.utils.StringUtils;
import tzuyu.engine.utils.Pair;
import tzuyu.plugin.tester.preferences.SearchScope;
import tzuyu.plugin.tester.preferences.TypeScope;
import tzuyu.plugin.tester.reporter.PluginLogger;

/**
 * @author LLT
 *
 */
public class TypeScopeParser {
	private static final String EMPTY_SEARCH_SCOPE = "{}";
	private static final String START = "{";
	private static final String END = "}";
	private static final String TYPE_SCOPE_SEPERATOR = "; ";
	private static final String TYPE_SCOPE_KEY_VALUE_SEPARATOR = "=";
	private static final String TYPE_SCOPE_START_IMPL_TYPES = "[";
	private static final String TYPE_SCOPE_END_IMPL_TYPES = "]";
	private static final String TYPE_SCOPE_IMPL_TYPES_SEPARATOR = ", ";
	
	private TypeScopeParser() {}
	
	public static String toString(Map<String, TypeScope> map) {
		if (map.isEmpty()) {
			return EMPTY_SEARCH_SCOPE;
		}
		StringBuilder sb = new StringBuilder().append(START);
		Iterator<Entry<String, TypeScope>> i = map.entrySet()
				.iterator();
		for (;;) {
			Entry<String, TypeScope> e = i.next();
			sb.append(typeScopeToString(e.getValue()));
			if (!i.hasNext())
				return sb.append(END).toString();
			sb.append(TYPE_SCOPE_SEPERATOR);
		}
	}
	
	public static String typeScopeToString(TypeScope typeScope) {
		StringBuilder sb = new StringBuilder();
		sb.append(typeScope.getFullyQualifiedName())
				.append(TYPE_SCOPE_KEY_VALUE_SEPARATOR)
				.append(typeScope.getScope().name());
		if (typeScope.getScope() == SearchScope.USER_DEFINED) {
			sb.append(TYPE_SCOPE_START_IMPL_TYPES);
			appendImplTypes(typeScope.getImplTypes(), sb);
			sb.append(TYPE_SCOPE_END_IMPL_TYPES);
		}
		return sb.toString();
	}
	
	public static void appendImplTypes(List<Pair<String, IType>> implTypes,
			StringBuilder sb) {
		List<String> typesStr = new ArrayList<String>(implTypes.size());
		for (Pair<String, IType> implType : implTypes) {
			typesStr.add(implType.a);
		}
		sb.append(StringUtils.join(typesStr, TYPE_SCOPE_IMPL_TYPES_SEPARATOR));
	}
	
	public static Map<String, TypeScope> parse(String str, IJavaProject project) {
		Map<String, TypeScope> map = new HashMap<String, TypeScope>();
		if (EMPTY_SEARCH_SCOPE.equals(str)) {
			return map;
		}
		// remove {}
		str = str.substring(1, str.length() - 1);
		for (String typeScopeStr : str.split(TYPE_SCOPE_SEPERATOR)) {
			TypeScope typeScope = toTypeScope(typeScopeStr, project);
			map.put(typeScope.getFullyQualifiedName(), typeScope);
		}
		return map;
	}

	private static TypeScope toTypeScope(String typeScopeStr, IJavaProject project) {
		TypeScope typeScope = new TypeScope();
		String[] split = typeScopeStr.split(TYPE_SCOPE_KEY_VALUE_SEPARATOR);
		// set full qualified name
		typeScope.setFullyQualifiedName(split[0]);
		
		// set search scope
		String scope = split[1];
		typeScope.setScope(toSearchScope(scope));
		
		// try to find iType for name
		try {
			typeScope.setType(project.findType(typeScope
					.getFullyQualifiedName()));
		} catch (JavaModelException e) {
			PluginLogger.getLogger().logEx(e);
			// do nothing
		}
		
		// set implementation types
		if (typeScope.getScope() == SearchScope.USER_DEFINED) {
			scope = scope.substring(
								scope.indexOf(TYPE_SCOPE_START_IMPL_TYPES) + 1,
								scope.indexOf(TYPE_SCOPE_END_IMPL_TYPES));
			if (!StringUtils.isEmpty(scope)) {

				for (String impStr : scope
						.split(TYPE_SCOPE_IMPL_TYPES_SEPARATOR)) {
					IType impType = null;
					try {
						impType = project.findType(impStr);
					} catch (JavaModelException e) {
						PluginLogger.getLogger().logEx(e);
						// ignore
					}
					typeScope.getImplTypes().add(Pair.of(impStr, impType));
				}
			}
		}
		return typeScope;
	}
	
	public static SearchScope toSearchScope(String str) {
		for (SearchScope scope : SearchScope.values()) {
			if (str.startsWith(scope.name())) {
				return scope;
			}
		}
		Assert.fail("Can not find scope with string: "+ str);
		return null; 
	}
}
