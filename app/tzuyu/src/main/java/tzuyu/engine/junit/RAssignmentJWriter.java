/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit;

import tester.ObjectType;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.junit.printer.JOutputPrinter;
import tzuyu.engine.model.Variable;
import tzuyu.engine.model.exception.TzRuntimeException;
import tzuyu.engine.runtime.RAssignment;
import tzuyu.engine.utils.ClassUtils;
import tzuyu.engine.utils.PrimitiveTypes;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class RAssignmentJWriter extends AbstractStmtJWriter {
	private String declaredClass;
	private String declaredName = StringUtils.EMPTY;
	private String instanceClass;
	private String val;
	
	public RAssignmentJWriter(TzConfiguration config, VariableRenamer renamer,
			RAssignment stmt, Variable newVar) {
		super(config, renamer);
		init(stmt, newVar);
	}
	
	public void init(RAssignment statement, Variable newVar) {
		Class<?> type = statement.getReturnType();
		declaredClass = type.getSimpleName();
		instanceClass = null;
		if (newVar != null) {
			declaredName = renamer.getRenamedVar(newVar.getStmtIdx(),
					newVar.getArgIdx());
		}
		val = null;
		if (statement.getValue() != null) {
			switch (ObjectType.ofClass(type)) {
			/** @see java.lang.Class#isPrimitiveType() **/
			case PRIMITIVE_TYPE: // int, double, char,...
				declaredClass = PrimitiveTypes.boxedType(type).getSimpleName();
				instanceClass = declaredClass;
				val = PrimitiveTypes.toCodeString(
						statement.getValue(), config.getStringMaxLength());
				break;
			case STRING_OR_PRIMITIVE_OBJECT: // String, Integer, Double,...
				declaredClass = ClassUtils.getSimpleCompilableName(type);
				val = PrimitiveTypes.toCodeString(
						statement.getValue(), config.getStringMaxLength());
				break;
			case ENUM:
				declaredClass = type.getSimpleName();
				val = StringUtils.enumToString(declaredClass, 
						statement.getValue());
				break;
			case GENERIC_ENUM: // Enum<?>
				declaredClass = statement.getValue().getClass()
						.getSimpleName();
				val = StringUtils.enumToString(declaredClass, 
						statement.getValue());
				break;
			case GENERIC_CLASS: // Class<?>
				declaredClass = Class.class.getSimpleName();
				val = ClassUtils.getClassNameWithSuffix(((Class<?>) statement.getValue()));
				break;
			default:
				throw new TzRuntimeException(
						"Can not build Assginment statement for type: "
								+ type.getName()
								+ " .Try to use Constructor statement instead!!");
			}
		}
	}
	
	public void write(JOutputPrinter sb) {
		sb.append(declaredClass).append(" ").append(declaredName).append(" = ");
		if (instanceClass != null) {
			sb.append("new ").append(instanceClass).append("(")
					.append(val)
					.append(")");
			
		} else {
			sb.append(val);
		};
	}
	
	public String getVal() {
		return val;
	}
}
