/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import icsetlv.common.utils.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 * 
 */
public class BreakPoint {
	private String classCanonicalName;
	private String methodName;
	private int lineNo = -1;
	private List<Variable> vars;
	private int charStart;
	private int charEnd;
	
	public BreakPoint(String className, String methodName) {
		this.classCanonicalName = className;
		this.methodName = methodName;
		vars = new ArrayList<Variable>();
	}
	
	public BreakPoint(String className, String methodName, int lineNo) {
		this(className, methodName);
		setLineNo(lineNo);
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
	
	@Override
	public String toString() {
		return "BreakPoint [classCanonicalName=" + classCanonicalName
				+ ", methodName=" + methodName + ", lineNo=" + lineNo
				+ ", vars=" + vars + ", charStart=" + charStart + ", charEnd="
				+ charEnd + "]";
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

		@Override
		public String toString() {
			return "Variable [name=" + name + ", code=" + code + ", scope="
					+ scope + "]";
		}
	}
	
	public static enum VarScope {
		THIS,
		LOCAL,
		UNKNOWN
	}
}
