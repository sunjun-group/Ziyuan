/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable;

import java.util.Collections;
import java.util.List;

import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.variables.Variable;
import slicer.javaslicer.IVariableCollectorContext;

/**
 * @author LLT
 *
 */
public class NullVariableContext implements IVariableCollectorContext {
	private static final NullVariableContext instance = new NullVariableContext();
	
	public static IVariableCollectorContext getInstance() {
		return instance;
	}

	@Override
	public void startContext(String locId) {
		// do nothing
	}

	@Override
	public void endContext() {
		// do nothing
	}

	@Override
	public void addLink(InstructionInstance instrIntance,
			InstructionInstance to, Variable variable) {
		// do nothing
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<sav.strategies.dto.BreakPoint.Variable> getVariables() {
		return Collections.EMPTY_LIST;
	}

}
