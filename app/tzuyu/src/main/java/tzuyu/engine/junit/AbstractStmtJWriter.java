/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.junit.printer.JOutputPrinter;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.StringUtils;
import tzuyu.engine.utils.TzUtils;

/**
 * @author LLT
 *
 */
public abstract class AbstractStmtJWriter {
	protected TzConfiguration config;
	protected VariableRenamer renamer;
	/* newClazz will be modified for other cases like: 
	 * b = A.new B();
	 * or  b = a.new B();
	 */
	protected String newClazzToken = "new ";  

	public AbstractStmtJWriter(TzConfiguration config, VariableRenamer renamer) {
		this.config = config;
		this.renamer = renamer;
	}
	
	public void writeCode(JOutputPrinter content) {
		write(content);
		content.append(";").append(Globals.lineSep);
	}
	
	public abstract void write(JOutputPrinter content);
	

	/**
	 * based on variable var,
	 * if, there is a declaration for var, paramStr will be the found declared variable.
	 * otherwise, the exact value of var will be returned. 
	 */
	protected String getParamStr(Variable var) {
		// In the short output format, statements like "int x = 3" are not
		// added
		// to a sequence; instead, the value (e.g. "3") is inserted directly
		// added as arguments to method calls.
		StatementKind stmt = TzUtils.getFirstDeclareStmt(var);
		String paramStr;
		if (!config.isLongFormat() && stmt instanceof RAssignment) {
			RAssignmentJWriter rAssWriter = new RAssignmentJWriter(config,
					renamer, (RAssignment) stmt, null);
			paramStr = rAssWriter.getVal();
		} else {
			paramStr = renamer.getRenamedVar(var.getStmtIdx(), var.getArgIdx());
		}
		return paramStr;
	}
	
	protected List<String> buildParamList(List<Variable> inputVars,
			List<Class<?>> methodInputTypes, boolean isStatic) {
		List<String> params = new ArrayList<String>();
		int startIndex = (isStatic ? 0 : 1);
		for (int i = startIndex; i < inputVars.size(); i++) {
			Class<?> type = methodInputTypes.get(i);
			Variable var = inputVars.get(i);
			// CASTING.
			// We cast whenever the variable and input types are not identical.
			// We also cast if input type is a primitive, because Randoop uses
			// boxed primitives, and need to convert back to primitive.
			String param = StringUtils.EMPTY;
			if (PrimitiveTypes.isPrimitive(type) && config.isLongFormat()) {
				param = "(" + type.getSimpleName() + ")";
			} else if (!var.getType().equals(type)) {
				param = "(" + type.getSimpleName() + ")";
			}

			param += getParamStr(var);
			params.add(param);
		}
		return params;
	}
}
