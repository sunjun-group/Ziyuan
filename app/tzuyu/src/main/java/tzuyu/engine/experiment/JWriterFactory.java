/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.experiment;

import java.util.List;

import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RMethod;

/**
 * @author LLT
 *
 */
public class JWriterFactory {
	private JunitConfig config;
	private VariableRenamer renamer;
	
	public JWriterFactory(JunitConfig config, VariableRenamer renamer) {
		this.config = config;
		this.renamer = renamer;
	}
	
	public RAssignmentJWriter createRAssgnmentWriter(RAssignment stmt, Variable newVar) {
		return new RAssignmentJWriter(config, renamer, stmt, newVar);
	}
	
	public RMethodJWriter createRMethodWriter(RMethod rmethod, Variable newVar,
			List<Variable> inputVars) {
		return new RMethodJWriter(config, renamer, rmethod, newVar, inputVars);
	}
	
	public static class JunitConfig {
		private int stringMaxLength;
		/*
		 *format junit file
		 * if long, we will have something like this:
		 * int x = a;
		 * method(x);
		 * if short format, it will turn into:
		 * method(a); 
		 */
		private boolean longFormat;
		
		public JunitConfig(int stringMaxLength, boolean longFormat) {
			this.stringMaxLength = stringMaxLength;
			this.longFormat = longFormat;
		}

		public int getStringMaxLength() {
			return stringMaxLength;
		}	
		
		public boolean isLongFormat() {
			return longFormat;
		}
	}
}
