/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.Constants;
import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class BreakPoint extends ClassLocation {
	protected List<Variable> vars; // use List instead of Set in order to keep the order of variables
	private int charStart;
	private int charEnd;
	
	public BreakPoint(String className, String methodSign, int lineNo) {
		super(className, methodSign, lineNo);
		vars = new ArrayList<Variable>();
	}
	
	public BreakPoint(String className, int lineNo, Variable... newVars) {
		this(className, null, lineNo);
		if (newVars != null) {
			addVars(newVars);
		}
	}
	
	public void addVars(Variable... newVars) {
		for (Variable newVar : newVars) {
			vars.add(newVar);
		}
	}
	
	public void addVars(List<Variable> newVars) {
		for (Variable newVar : newVars) {
			CollectionUtils.addIfNotNullNotExist(vars, newVar);
		}
	}
	
	public List<Variable> getVars() {
		return vars;
	}

	public void setVars(List<Variable> vars) {
		this.vars = vars;
	}
	
	public boolean valid() {
		return lineNo > 0;
	}
	
	public String getMethodName() {
		Assert.notNull(methodName, "missing method name!");
		return methodName;
	}
	
	public int getCharStart() {
		return charStart;
	}

	public void setCharStart(int charStart) {
		this.charStart = charStart;
	}

	public int getCharEnd() {
		return charEnd;
	}

	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}
	
	public List<Integer> getOrgLineNos() {
		return Arrays.asList(lineNo);
	}

	@Override
	public String toString() {
		return "BreakPoint [classCanonicalName=" + classCanonicalName
				+ ", methodName=" + methodName + ", lineNo=" + lineNo
				+ ", vars=" + vars + ", charStart=" + charStart + ", charEnd="
				+ charEnd + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((classCanonicalName == null) ? 0 : classCanonicalName
						.hashCode());
		result = prime * result + lineNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BreakPoint other = (BreakPoint) obj;
		if (classCanonicalName == null) {
			if (other.classCanonicalName != null)
				return false;
		} else if (!classCanonicalName.equals(other.classCanonicalName))
			return false;
		if (lineNo != other.lineNo)
			return false;
		return true;
	}

	public static class Variable {
		/* 
		 * very first variable, 
		 * if var is a.b.c, root is a
		 * if var is a, root is a
		 * */
		private final String root;
		/* including its object if variable is an object's field
		 * ex: objA.fieldX
		 */
		private final String fullName;
		// specify whether the variable is belonged to class object or method.
		private final VarScope scope;
		private String id;
		private String type;
		
		public Variable(String root, String fullName, VarScope scope) {
			this.root = root;
			this.fullName = fullName;
			this.scope = scope;
		}
		
		public Variable(String root, String fullName) {
			this(root, fullName, VarScope.UNDEFINED);
		}

		public Variable(String name) {
			this.fullName = name;
			int i = fullName.indexOf(Constants.DOT);
			if (i >= 0) {
				root = fullName.substring(0, i);
			} else {
				root = name;
			}
			scope = VarScope.UNDEFINED;
		}

		public String getRoot() {
			return root;
		}
		
		public String getFullName() {
			return fullName;
		}

		private String simpleName;
		public String getSimpleName() {
			if (simpleName == null) {
				int l = fullName.lastIndexOf(Constants.DOT);
				simpleName = fullName.substring(l + 1);
			}
			return simpleName;
		}
		
		public String getId() {
			if (id == null) {
				id = genId(scope, fullName);
			}
			return id;
		}
		
		public static String genId(VarScope scope, String name) {
			return StringUtils.dotJoin(scope.getDisplayName(), name);
		}

		public VarScope getScope() {
			return scope;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "Variable [root=" + root + ", fullName=" + fullName
					+ ", scope=" + scope + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fullName == null) ? 0 : fullName.hashCode());
			result = prime * result + ((scope == null) ? 0 : scope.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Variable other = (Variable) obj;
			if (fullName == null) {
				if (other.fullName != null)
					return false;
			} else if (!fullName.equals(other.fullName))
				return false;
			if (scope != other.scope)
				return false;
			return true;
		}



		public static enum VarScope {
			THIS ("this"),
			UNDEFINED ("");
			private String displayName;
			
			private VarScope(String displayName) {
				this.displayName = displayName;
			}
			
			public String getDisplayName() {
				return displayName;
			}
		}
	}
	
}
