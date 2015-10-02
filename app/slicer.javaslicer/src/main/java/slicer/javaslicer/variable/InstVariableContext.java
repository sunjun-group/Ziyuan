/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.SavRtException;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstanceInfo;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.ArrayInstruction.ArrayInstrInstanceInfo;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.FieldInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.IIncInstruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.VarInstruction;

/**
 * @author LLT
 *
 */
public class InstVariableContext {
	private List<Variable> variables;
	private InstructionVariableState state;
	private StatePool statePool;
	
	public InstVariableContext() {
		variables = new ArrayList<Variable>();
		state = new NormalState(this);
		statePool = new StatePool();
	}
	
	public void startContext() {
		state = createState(StateId.NORMAL, null);
	}
	
	public void endContext() {
		variables.clear();
		statePool.release(state);
		state = null;
	}
	
	public void accessInstruction(Instruction instruction,
			InstructionInstanceInfo instrInfo) {
	switch (instruction.getType()) {
		case ARRAY:
			state.accessInstruction((ArrayInstruction)instruction, (ArrayInstrInstanceInfo)instrInfo);
			break;
		case FIELD:
			state.accessInstruction((FieldInstruction)instruction);
			break;
		case VAR:
			state.accessInstruction((VarInstruction)instruction);
			break;
		case IINC:
			state.accessInstruction((IIncInstruction)instruction);
		default:
			break;
		}
	}

	public void setState(InstructionVariableState state) {
		statePool.release(this.state);
		this.state = state;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends InstructionVariableState>T createState(StateId id, InstructionVariableState parentState) {
		T newState = (T) statePool.create(id);
		newState.setParentState(parentState);
		return newState;
	}

	public void addVariable(String fullName, boolean objectInstance) {
		Variable variable = new Variable(fullName, fullName,
				objectInstance ? VarScope.THIS : VarScope.UNDEFINED);
		variables.add(variable);
	}
	
	public List<Variable> getVariables() {
		return new ArrayList<Variable>(variables);
	}
	
	/*
	 * a very simple version of object pool, just to cache states, so that we won't
	 * have to initialize so many state objects
	 */
	private class StatePool {
		private Map<StateId, List<InstructionVariableState>> states;
		
		public StatePool() {
			states = new HashMap<StateId, List<InstructionVariableState>>();
			for (StateId id : StateId.values()) {
				states.put(id, new ArrayList<InstructionVariableState>());
			}
		}

		public synchronized void release(InstructionVariableState state) {
			state.release();
			states.get(state.getStateId()).add(state);
		}

		public synchronized InstructionVariableState create(StateId id) {
			List<InstructionVariableState> existingStates = states.get(id);
			InstructionVariableState state = null;
			if (existingStates.isEmpty()) {
				state = newState(id);
			} else {
				state = existingStates.remove(0);
			}
			return state;
		}

		private InstructionVariableState newState(StateId id) {
			switch (id) {
			case NORMAL:
				return new NormalState(InstVariableContext.this);
			case ARRAY_ACCESS:
				return new ArrayAccessState(InstVariableContext.this);
			case FIELD_ACCESS:
				return new FieldAccessState(InstVariableContext.this);
			}
			throw new SavRtException("Cannot identify InstVariableContext.StateId " + id);
		}
	}
	
	public enum StateId {
		NORMAL,
		FIELD_ACCESS,
		ARRAY_ACCESS
	}
}
