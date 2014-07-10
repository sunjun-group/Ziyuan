/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.analyzer.marker;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.utils.Assert;

/**
 * @author LLT
 * 
 */
public class BreakPoint {
	private String classCanonicalName;
	private String methodName;
	private int lineNo = -1;
	private List<Variable> vars;
	
	public BreakPoint(String className, String methodName) {
		this.classCanonicalName = className;
		this.methodName = methodName;
		vars = new ArrayList<Variable>();
	}
	
	public BreakPoint(String className, int lineNo, Variable... newVars) {
		this(className, null);
		this.lineNo = lineNo;
		if (newVars != null) {
			addVars(newVars);
		}
	}

	public void addVars(Variable... newVars) {
		for (Variable newVar : newVars) {
			vars.add(newVar);
		}
	}
	
	public String getClassCanonicalName() {
		return classCanonicalName;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
		Assert.assertNotNull(methodName, "missing method name!");
		return methodName;
	}

	public static class Variable {
		private String name;
		private String code;
		private VarScope scope;

		public Variable() {
			scope = VarScope.UNKNOWN;
		}

		public Variable(String name) {
			this();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			if (code == null) {
				return name;
			}
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public VarScope getScope() {
			return scope;
		}

		public void setScope(VarScope scope) {
			this.scope = scope;
		}
	}
	
	public static enum VarScope {
		THIS,
		LOCAL,
		UNKNOWN
	}
}
