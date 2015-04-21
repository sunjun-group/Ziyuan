/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.parser;

import java.util.Map;

/**
 * @author LLT
 * 
 */
public class VariableScope {
	private int startLine;
	private int endLine;
	private Map<String, VariableDescriptor> vars;

	public VariableScope(int startLine, int endLine, Map<String, VariableDescriptor> vars) {
		this.startLine = startLine;
		this.endLine = endLine;
		this.vars = vars;
	}
	
	public VariableDescriptor put(String key, VariableDescriptor value) {
		return vars.put(key, value);
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public Map<String, VariableDescriptor> getVars() {
		return vars;
	}

	public void setVars(Map<String, VariableDescriptor> vars) {
		this.vars = vars;
	}

}
