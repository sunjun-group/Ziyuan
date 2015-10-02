/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.variable;

import sav.common.core.utils.Assert;
import slicer.javaslicer.variable.InstVariableContext.StateId;
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
public class NormalState extends AbstractVariableState {
	
	public NormalState(InstVariableContext context) {
		super(context);
	}
	
	public void accessInstruction(VarInstruction instruction) {
		LocalVariable localVariable = getLocalVarName(instruction);
		boolean thisOjbRef = isThisOjbRef(localVariable);
		Assert.assertTrue(!thisOjbRef);
		if (!thisOjbRef) {
			addNewVariable(localVariable.getName(), false);
		}
	}
	
	@Override
	public void addNewVariable(String name, boolean isThisObjRef) {
		context.addVariable(name, isThisObjRef);
	}

	public void accessInstruction(FieldInstruction instruction) {
		FieldAccessState newState = createFieldAccessState(instruction);
		context.setState(newState);
	}

	protected FieldAccessState createFieldAccessState(FieldInstruction instruction) {
		FieldAccessState newState = context.createState(StateId.FIELD_ACCESS, this);
		newState.setParentState(this);
		newState.enter(instruction.getFieldName());
		return newState;
	}
	
	public void accessInstruction(ArrayInstruction instruction,
			ArrayInstrInstanceInfo instrInfo) {
		ArrayAccessState newState = context.createState(StateId.ARRAY_ACCESS, this);
		newState.enter(instrInfo);
		newState.setParentState(this);
		context.setState(newState);
	}
	
	@Override
	public void accessInstruction(IIncInstruction instruction) {
		LocalVariable localVar = getLocalVarName(instruction, instruction.getLocalVarIndex());
		context.addVariable(localVar.getName(), false);
	}
	
	@Override
	public StateId getStateId() {
		return StateId.NORMAL;
	}

	@Override
	public void release() {
	
	}
}
