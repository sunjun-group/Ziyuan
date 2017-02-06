/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.List;

import sav.strategies.dto.BreakPoint.Variable;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;

/**
 * @author LLT
 *
 */
public interface IVariableCollectorContext {

	void startContext(String locId);

	void endContext();

	void addLink(InstructionInstance from, InstructionInstance to,
			de.unisb.cs.st.javaslicer.variables.Variable variable);

	List<Variable> getVariables();

}
