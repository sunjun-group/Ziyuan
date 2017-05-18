/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis;

import org.jacoco.core.internal.analysis.AbstractMethodAnalyzer;
import org.jacoco.core.internal.flow.Instruction;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author LLT
 *
 */
public class EmptyMethodAnalyzer extends AbstractMethodAnalyzer {

	public EmptyMethodAnalyzer(String className, String superClassName) {
		super(className, superClassName);
	}

	@Override
	protected Instruction createInsn(AbstractInsnNode node, int line) {
		return new Instruction(node, line);
	}

	@Override
	protected void addProbe(int probeId) {
		// do nothing
	}

}
