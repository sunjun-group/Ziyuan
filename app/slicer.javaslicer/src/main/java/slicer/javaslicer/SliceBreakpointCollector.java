/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sav.common.core.Logger;
import sav.strategies.dto.BreakPoint;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadClass;
import de.unisb.cs.st.javaslicer.slicing.SliceVisitor;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT
 *
 */
public class SliceBreakpointCollector implements SliceVisitor {
	private Logger<?> log = Logger.getDefaultLogger();
	private Set<BreakPoint> dynamicSlice = new HashSet<BreakPoint>();

	private void add(Instruction instruction) {
		if (isAccepted(instruction)) {
			ReadClass clazz = instruction.getMethod().getReadClass();
			BreakPoint bkp = new BreakPoint(clazz.getName(), instruction.getMethod()
					.getName(), instruction.getLineNumber());
			dynamicSlice.add(bkp);
		}
	}
	
	protected boolean isAccepted(Instruction instruction) {
		return true;
	}
	
	@Override
	public void visitMatchedInstance(InstructionInstance instance) {
		add(instance.getInstruction());
	}

	@Override
	public void visitSliceDependence(InstructionInstance from,
			InstructionInstance to, Variable variable, int distance) {
		add(to.getInstruction());
	}

	public List<BreakPoint> getDynamicSlice() {
		return new ArrayList<BreakPoint>(dynamicSlice);
	}
	
	public void reset() {
		dynamicSlice.clear();
	}
}
