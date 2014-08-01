/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.tester.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;

import sav.common.core.utils.StringUtils;
import tzuyu.engine.utils.Assert;
import tzuyu.engine.utils.Pair;
import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.tester.command.gentest.TypeScopeParser;

/**
 * @author LLT
 *
 */
public class TypeScope {
	private String fullyQualifiedName;
	private IType type;
	private SearchScope scope;
	private List<Pair<String, IType>> implTypes;
	
	public TypeScope() {
		scope = SearchScope.SOURCE;
		implTypes = new ArrayList<Pair<String,IType>>();
	}
	
	public IType getType() {
		return type;
	}

	public void setType(IType type) {
		this.type = type;
		if (type != null) {
			this.fullyQualifiedName = type.getFullyQualifiedName();
		}
	}

	public SearchScope getScope() {
		return scope;
	}

	public void setScope(SearchScope scope) {
		this.scope = scope;
	}
	
	public List<Pair<String, IType>> getImplTypes() {
		return implTypes;
	}

	public void setImplTypes(List<Pair<String, IType>> implTypes) {
		this.implTypes = implTypes;
	}
	
	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}
	
	public String getFullyQualifiedName() {
		Assert.assertNotNull(fullyQualifiedName,
				"fullyQualifiedName or type must be set for typeScope!!");
		return fullyQualifiedName;
	}

	public String getDisplayType() {
		return getDisplayString(type, fullyQualifiedName);
	}

	public String getDisplayScope() {
		if (type == null) {
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder(TzuyuPlugin.getMessages()
				.getMessage(scope));
		if (scope == SearchScope.USER_DEFINED) {
			sb.append(" (");
			TypeScopeParser.appendImplTypes(implTypes, sb);
			sb.append(")");
		}
		return sb.toString();
	}

	public static String getDisplayString(IType type, String defaultName) {
		if (type == null) {
			return "[Missing]" + defaultName;
		}
		return type.getElementName() + " (" + type.getFullyQualifiedName()
				+ ")";
	}
	
	@Override
	public String toString() {
		return TypeScopeParser.typeScopeToString(this);
	}

	public boolean hasError() {
		if (type == null) {
			return true;
		}
		for (Pair<String, IType> implPair : implTypes) {
			if (implPair.b == null) {
				return true;
			}
		}
		return false;
	}
}
