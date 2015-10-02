/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.variable;

import slicer.javaslicer.variable.InstVariableContext.StateId;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction.ArrayInstrInstanceInfo;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.FieldInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.IIncInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.VarInstruction;

/**
 * @author LLT
 *
 */
public interface InstructionVariableState {
	public InstructionVariableState getParentState();

	public void accessInstruction(VarInstruction instruction);

	public void accessInstruction(FieldInstruction instruction);

	public void accessInstruction(ArrayInstruction instruction, ArrayInstrInstanceInfo instrInfo);
	
	public StateId getStateId();
	
	public void release();

	public void setParentState(InstructionVariableState parentState);

	public void accessInstruction(IIncInstruction instruction);
	
	public void addNewVariable(String name, boolean isThisObjRef);
}
