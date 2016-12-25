/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable;

import sav.common.core.utils.BreakpointUtils;
import sav.strategies.dto.BreakPoint;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;

/**
 * @author LLT
 *
 */
public class InstructionUtils {
	private InstructionUtils(){}

	public static String getLocationId(Instruction instruction) {
		return BreakpointUtils.getLocationId(instruction.getMethod()
				.getReadClass().getName(), instruction.getLineNumber());
	}
	
	public static String getLocationId(InstructionInstance instrInst) {
		return getLocationId(instrInst.getInstruction());
	}

	public static BreakPoint getBreakpoint(Instruction instruction) {
		return new BreakPoint(instruction.getMethod().getReadClass().getName(),
				instruction.getMethod().getName(), instruction.getLineNumber());
	}
	
}
