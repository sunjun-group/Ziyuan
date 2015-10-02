/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.variable;

import slicer.javaslicer.variable.InstVariableContext.StateId;
import de.unisb.cs.st.javaslicer.common.classRepresentation.LocalVariable;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.FieldInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.VarInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction.ArrayInstrInstanceInfo;

/**
 * @author LLT
 *
 */
public class FieldAccessState extends NormalState {
	private String fieldName;
	
	public FieldAccessState(InstVariableContext context) {
		super(context);
	}
	
	public void enter(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public void accessInstruction(VarInstruction instruction) {
		LocalVariable scope = getLocalVarName(instruction);
		if ("this".endsWith(scope.getName())) {
			getParentState().addNewVariable(fieldName, true);
		} else {
			getParentState().addNewVariable(
					getFullName(scope.getName(), fieldName), false);
		}
		context.setState(getParentState());
	}
	
	@Override
	public void addNewVariable(String name, boolean isThisObjRef) {
		super.addNewVariable(getFullName(name, fieldName), isThisObjRef);
	}

	@Override
	public void accessInstruction(FieldInstruction instruction) {
		fieldName = getFullName(instruction.getFieldName(), fieldName);
	}

	@Override
	public void accessInstruction(ArrayInstruction instruction, ArrayInstrInstanceInfo instrInfo) {
		super.accessInstruction(instruction, instrInfo);
	}
	
	@Override
	public StateId getStateId() {
		return StateId.FIELD_ACCESS;
	}

	@Override
	public void release() {
		fieldName = null;
	}
}
