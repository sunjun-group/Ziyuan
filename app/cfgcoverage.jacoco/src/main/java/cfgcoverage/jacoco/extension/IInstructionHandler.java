/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.extension;

import org.jacoco.core.internal.flow.Instruction;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * @author LLT
 *
 */
public interface IInstructionHandler {

	Instruction createInstruction(AbstractInsnNode currentNode, int currentLine);
	
}
