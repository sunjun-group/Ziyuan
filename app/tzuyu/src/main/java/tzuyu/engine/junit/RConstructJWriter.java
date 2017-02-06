/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.junit;

import java.lang.reflect.Modifier;
import java.util.List;

import sav.common.core.utils.Assert;
import sav.common.core.utils.StringUtils;
import tzuyu.engine.TzConfiguration;
import tzuyu.engine.junit.printer.JOutputPrinter;
import tzuyu.engine.model.Variable;
import tzuyu.engine.runtime.RConstructor;
import tzuyu.engine.utils.ClassUtils;
import tzuyu.engine.utils.LogicUtils;

/**
 * @author LLT
 * 
 */
public class RConstructJWriter extends AbstractStmtJWriter {
	private String declaredClass;
	private String declaredName;
	private String instanceClass;
	private List<String> params;
	
	public RConstructJWriter(TzConfiguration config, VariableRenamer renamer,
			RConstructor ctor, Variable newVar, List<Variable> inputVars) {
		super(config, renamer);
		init(ctor, newVar, inputVars);
	}

	private void init(RConstructor ctor, Variable newVar,
			List<Variable> inputVars) {
		List<Class<?>> methodInputTypes = ctor.getInputTypes();
		Assert.assertTrue(inputVars.size() == methodInputTypes.size());
		Class<?> declaringClass = ctor.getConstructor().getDeclaringClass();
		boolean isNonStaticMember = !Modifier.isStatic(declaringClass
				.getModifiers()) && declaringClass.isMemberClass();
		Assert.assertTrue(LogicUtils.implies(isNonStaticMember, inputVars.size() > 0));
		// Note on isNonStaticMember: if a class is a non-static member class,
		// the runtime signature of the constructor will have an additional
		// argument (as the first argument) corresponding to the owning object.
		// When printing it out as source code, we need to treat it as a special
		// case: instead of printing "new Foo(x,y,z)" we have to print
		// "x.new Foo(y,z)".
		declaredClass = ClassUtils.getSimpleCompilableName(declaringClass);
		declaredName = renamer.getRenamedVar(newVar.getStmtIdx(), newVar.getArgIdx());
		if (isNonStaticMember) {
			newClazzToken = StringUtils.join(".", renamer
					.getRenamedVar(inputVars.get(0).getStmtIdx(), inputVars
							.get(0).getArgIdx()), newClazzToken);
			instanceClass = declaringClass.getSimpleName();
		} else {
			instanceClass = declaredClass;
		}
		
		params = buildParamList(inputVars, methodInputTypes, !isNonStaticMember);
	}

	@Override
	public void write(JOutputPrinter sb) {
		sb.append(declaredClass).append(" ").append(declaredName).append(" = ");
		sb.append(newClazzToken).append(instanceClass).append("(");
		sb.append(StringUtils.join(params, ", "));
		sb.append(")");
	}

}
