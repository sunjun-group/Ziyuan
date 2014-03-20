/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;

import tzuyu.engine.utils.CollectionUtils;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class TypeScope {
	private String fullyQualifiedName;
	private IType type;
	private SearchScope scope;
	private List<IType> implTypes;
	
	public TypeScope() {
		scope = SearchScope.SOURCE;
		implTypes = new ArrayList<IType>();
	}
	
	public IType getType() {
		return type;
	}

	public void setType(IType type) {
		this.type = type;
	}

	public SearchScope getScope() {
		return scope;
	}

	public void setScope(SearchScope scope) {
		this.scope = scope;
	}

	public List<IType> getImplTypes() {
		return implTypes;
	}

	public void setImplTypes(List<IType> implTypes) {
		this.implTypes = CollectionUtils.nullToEmpty(implTypes);
	}
	
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public String getDisplayType() {
		return getDisplayString(type);
	}

	public String getDisplayImplTypes() {
		List<String> typesStr = new ArrayList<String>(implTypes.size());
		for (IType implType : implTypes) {
			typesStr.add(implType.getFullyQualifiedName()); 
		}
		return StringUtils.join(typesStr, ", ");
	}

	public static String getDisplayString(IType type) {
		return type.getElementName() + " (" + type.getFullyQualifiedName()
				+ ")";
	}
}
