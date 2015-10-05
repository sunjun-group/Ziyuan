/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable;

import sav.common.core.utils.StringUtils;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.LocalVariable;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction.ArrayInstrInstanceInfo;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.FieldInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.IIncInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.VarInstruction;

/**
 * @author LLT
 *
 */
public abstract class AbstractVariableState implements InstructionVariableState {
	private InstructionVariableState parentState;
	protected InstVariableContext context;
	
	public AbstractVariableState(InstVariableContext context) {
		this.context = context;
	}
	
	public abstract void addNewVariable(String name, boolean isThisObjRef);
	
	@Override
	public abstract void accessInstruction(VarInstruction instruction);

	@Override
	public abstract void accessInstruction(FieldInstruction instruction);

	@Override
	public abstract void accessInstruction(ArrayInstruction instruction, ArrayInstrInstanceInfo instrInfo);
	
	@Override
	public void accessInstruction(IIncInstruction instruction) {
		// TODO Auto-generated method stub
	}
	
	protected boolean isThisOjbRef(LocalVariable var) {
		return "this".equals(var.getName());
	}
	
	protected LocalVariable getLocalVarName(VarInstruction instruction) {
		int varIdx = instruction.getLocalVarIndex();
		return getLocalVarName(instruction, varIdx);
	}

	protected LocalVariable getLocalVarName(Instruction instruction, int varIdx) {
		LocalVariable localVariable = instruction.getMethod().getLocalVariables()[varIdx];
		return localVariable;
	}
	
	protected String getFullName(String scope, String fieldName) {
		return StringUtils.dotJoin(scope, fieldName);
	}
	
	protected boolean matchOpCodes(int opCode, int... opCodes) {
		for (int code : opCodes) {
			if (code == opCode) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public InstructionVariableState getParentState() {
		return parentState;
	}
	
	@Override
	public void setParentState(InstructionVariableState parentState) {
		this.parentState = parentState;
	}
	
	@Override
	public void release() {
		// do nothing by default
	}

}
