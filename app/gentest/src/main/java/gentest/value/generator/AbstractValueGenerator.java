/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import java.lang.reflect.Type;

import gentest.data.statement.RAssignment;
import gentest.data.variable.GeneratedVariable;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public abstract class AbstractValueGenerator {
	/* level of generated value from root statment
	 * ex: generate value for parameter p1 of method:
	 * methodA(List<Interger> p1, p2)
	 * we do 2 generation step:
	 * generate list -> level 1
	 * generate values for list -> level 2
	 * */
	protected static int maxLevel = 4;

	public static GeneratedVariable generate(Class<?> clazz, Type type, int firstStmtIdx,
			int firstVarId) throws SavException {
		GeneratedVariable variable = new GeneratedVariable(firstStmtIdx,
				firstVarId);
		append(variable, 1, clazz, type);
		return variable;
	}

	public static void append(GeneratedVariable variable, int level,
			Class<?> clazz, Type type) throws SavException {
		if (level > maxLevel) {
			variable.append(RAssignment.assignmentFor(clazz, null));
			return;
		}

		AbstractValueGenerator generator = findGenerator(clazz, type);
		generator.doAppend(variable, level, clazz);
	}

	public abstract void doAppend(GeneratedVariable variable, int level,
			Class<?> type) throws SavException;

	private static AbstractValueGenerator findGenerator(Class<?> clazz, Type type) {
		if (PrimitiveValueGenerator.accept(clazz)) {
			return new PrimitiveValueGenerator();
		} 
		if (clazz.isArray()) {
			return new ArrayValueGenerator();
		}
		if (ListValueGenerator.accept(clazz)) {
			return new ListValueGenerator(clazz, type);
		}
		
		return getGenerator(ObjectValueGenerator.class);
	}
	
	/*TODO LLT: temp, to complete*/
	protected static AbstractValueGenerator getGenerator(Class<?> clazz) {
		if (clazz == ObjectValueGenerator.class) {
			return new ObjectValueGenerator();
		}
		return null;
	}
}
