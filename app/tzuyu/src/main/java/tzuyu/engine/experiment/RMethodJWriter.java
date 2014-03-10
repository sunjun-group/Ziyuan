/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.experiment;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.experiment.JWriterFactory.JunitConfig;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.ClassUtils;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.StringUtils;
import tzuyu.engine.utils.TzUtils;

/**
 * @author LLT
 *
 */
public class RMethodJWriter extends AbstractJWriter {
	private String returnedType;
	private String varReturnedName;
	private String instanceNameOrClass;
	private String methodName;
	private String classCast;
	private String typeArguments;
	private List<String> params;
	
	public RMethodJWriter(JunitConfig config, VariableRenamer renamer,
			RMethod rmethod, Variable newVar, List<Variable> inputVars) {
		super(config, renamer);
		init(rmethod, newVar, inputVars);
	}

	private void init(RMethod rmethod, Variable newVar,
			List<Variable> inputVars) {
		if (!rmethod.isVoid()) {
			returnedType = ClassUtils.getSimpleCompilableName(rmethod.getMethod()
					.getReturnType());
			varReturnedName = renamer.getRenamedVar(newVar.getStmtIdx(), newVar.getArgIdx());
		}
		if (rmethod.isStatic()) {
			instanceNameOrClass = rmethod.getMethod().getDeclaringClass().getSimpleName()
					.replace('$', '.'); // TODO combine this with last if clause
		} else {
			instanceNameOrClass = renamer
					.getRenamedVar(inputVars.get(0).getStmtIdx(),
							inputVars.get(0).getArgIdx());
			Class<?> expectedType = rmethod.getInputTypes().get(0);
			String className = expectedType.getSimpleName();
			boolean mustCast = className != null
					&& PrimitiveTypes
							.isBoxedPrimitiveTypeOrString(expectedType)
					&& !expectedType.equals(String.class);
			if (mustCast) {
				// this is a little paranoid but we need to cast primitives in
				// order to get them boxed.
				classCast = className;
			}
		}
		typeArguments = rmethod.getTypeArguments();
		methodName = rmethod.getMethod().getName();
		// init params
		params = new ArrayList<String>();
		int startIndex = (rmethod.isStatic() ? 0 : 1);
		for (int i = startIndex; i < inputVars.size(); i++) {
			Class<?> type = rmethod.getInputTypes().get(i);
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

			// In the short output format, statements like "int x = 3" are not
			// added
			// to a sequence; instead, the value (e.g. "3") is inserted directly
			// added as arguments to method calls.
			StatementKind stmt = TzUtils.getFirstDeclareStmt(var);
			if (!config.isLongFormat() && stmt instanceof RAssignment) {
				RAssignmentJWriter rAssWriter = new RAssignmentJWriter(config, renamer, (RAssignment)stmt,
						null);
				param += rAssWriter.getVal();
			} else {
				param += renamer.getRenamedVar(var.getStmtIdx(), var.getArgIdx());
			}
			params.add(param);
		}
	}
	
	/**
	 * cases:
	 * 1. void
	 * 		{instanceName}.{methodName}({varType1 var1, varType2 var2});
	 * 2. !void
	 * 		{returnedType} {varReturnedName} = {instanceName/class(if static)}.
	 * 					{methodName}({varType1 var1, varType2 var2});
	 * has type argument:
	 * 		{returnedType} {varReturnedName} = {instanceName/class(if static)}.
	 * 					<{typeArguments}>{methodName}({varType1 var1, varType2 var2});
	 * 
	 */
	public void writeCode(StringBuilder sb) {
		if (returnedType != null) {
			sb.append(returnedType).append(" ")
					.append(varReturnedName)
					.append(" = ");
		}
		if (classCast != null) {
			// instanceNameOrClass must be an instanceName (means not static method)
			sb.append("((").append(classCast).append(")").append(instanceNameOrClass)
						.append(")");
		} else {
			sb.append(instanceNameOrClass);
		}
		sb.append(".");
		sb.append(StringUtils.nullToEmpty(typeArguments));
		sb.append(methodName).append("(");
		sb.append(StringUtils.join(params, ", "));
		sb.append(");");
		sb.append(Globals.lineSep);
	}
}
