/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.experiment.tcbuilder;

import tzuyu.engine.utils.CollectionUtils;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class StatementBuilder {

	/**
	 * {declaredClass} {declaredName} = new {instanceClass}({vals});
	 * or
	 * {declaredClass} {declaredName} = {vars};
	 */
	public static void appendRAssignmentStmt(StringBuilder sb,
			String declaredClass, String declaredName, String instanceClass,
			String[] vals) {
		sb.append(declaredClass).append(" ").append(declaredName).append(" = ");
		if (instanceClass != null) {
			sb.append("new ").append(instanceClass).append("(")
					.append(StringUtils.join(", ", (Object[])vals))
					.append(")");
			
		} else {
			sb.append(CollectionUtils.getFirstElement(vals));
		}
		sb.append(";").append(Globals.lineSep);
	}
}
