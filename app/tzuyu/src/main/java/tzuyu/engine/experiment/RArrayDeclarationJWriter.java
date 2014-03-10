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
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RArrayDeclaration;
import tzuyu.engine.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class RArrayDeclarationJWriter extends AbstractJWriter {
	private String declaredClass;
	private String declaredName;
	private List<String> params;

	public RArrayDeclarationJWriter(JunitConfig config,
			VariableRenamer renamer, RArrayDeclaration rArrayDeclaration,
			Variable newVar, List<Variable> inputVars) {
		super(config, renamer);
		init(rArrayDeclaration, newVar, inputVars);
	}

	private void init(RArrayDeclaration statement, Variable newVar,
			List<Variable> inputVars) {
		// TODO LLT: assert fail in some cases, check!! 
//		int length = statement.getLength();
//		Assert.assertTrue(inputVars.size() > length, "Too many arguments:"
//				+ inputVars.size() + " capacity:" + length);
		declaredClass = statement.getElementType().getSimpleName();
		declaredName = renamer.getRenamedVar(newVar.getStmtIdx(), newVar.getArgIdx());
		params = new ArrayList<String>();
		for (int i = 0; i < inputVars.size(); i++) {
			Variable var = inputVars.get(i);
			params.add(getParamStr(var));
		}
	}

	@Override
	public void write(StringBuilder sb) {
		sb.append(declaredClass).append("[] ").append(declaredName).append(" = ")
			.append(newClazzToken).append(declaredClass).append("[]{ ");
		sb.append(StringUtils.join(params, ", "));
		sb.append("}");
	}

}
