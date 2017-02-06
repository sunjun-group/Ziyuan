/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit;

import java.util.List;

import sav.common.core.utils.StringUtils;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.junit.printer.JOutputPrinter;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RMethod;
import tzuyu.engine.utils.ClassUtils;
import tzuyu.engine.utils.PrimitiveTypes;

/**
 * @author LLT
 *
 */
public class RMethodJWriter extends AbstractStmtJWriter {
	private String returnedType;
	private String varReturnedName;
	private String instanceNameOrClass;
	private String methodName;
	private String classCast;
	private String typeArguments;
	private List<String> params;
	
	public RMethodJWriter(TzConfiguration config, VariableRenamer renamer,
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
		List<Class<?>> methodInputTypes = rmethod.getInputTypes();
		boolean isStatic = rmethod.isStatic();
		if (isStatic) {
			instanceNameOrClass = rmethod.getMethod().getDeclaringClass().getSimpleName()
					.replace('$', '.'); 
		} else {
			instanceNameOrClass = renamer
					.getRenamedVar(inputVars.get(0).getStmtIdx(),
							inputVars.get(0).getArgIdx());
			Class<?> expectedType = methodInputTypes.get(0);
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
		params = buildParamList(inputVars, methodInputTypes, isStatic);
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
	public void write(JOutputPrinter sb) {
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
		sb.append(")");
	}
}
