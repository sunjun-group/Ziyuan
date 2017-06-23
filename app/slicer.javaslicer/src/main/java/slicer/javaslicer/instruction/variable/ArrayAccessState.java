/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable;

import slicer.javaslicer.instruction.variable.InstVariableContext.StateId;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.FieldInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.VarInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction.ArrayInstrInstanceInfo;

/**
 * @author LLT
 * 
 */
public class ArrayAccessState extends NormalState {
	private int arrIdx;
	
	public void enter(ArrayInstrInstanceInfo instrInfo) {
		this.arrIdx = instrInfo.getArrayIndex();
	}

	public ArrayAccessState(InstVariableContext context) {
		super(context);
	}

	@Override
	public void accessInstruction(VarInstruction instruction) {
		addNewVariable(getLocalVarName(instruction).getName(), false);
	}
	
	@Override
	public void addNewVariable(String name, boolean isThisObjRef) {
		String arrVarName = String.format("%s[%s]", name, arrIdx);
		context.setState(getParentState());
		getParentState().addNewVariable(arrVarName, isThisObjRef);
	}

	@Override
	public void accessInstruction(FieldInstruction instruction) {
		super.accessInstruction(instruction);
	}

	@Override
	public void accessInstruction(ArrayInstruction instruction, ArrayInstrInstanceInfo instrInfo) {
		super.accessInstruction(instruction, instrInfo);
	}

	@Override
	public StateId getStateId() {
		return StateId.ARRAY_ACCESS;
	}
	
	@Override
	public void release() {
		arrIdx = -1;
	}

}
