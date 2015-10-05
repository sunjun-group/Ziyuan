/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sav.strategies.dto.BreakPoint;
import slicer.javaslicer.instruction.variable.InstructionUtils;
import slicer.javaslicer.instruction.variable.NullVariableContext;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.slicing.SliceVisitor;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT
 *
 */
public class SliceBreakpointCollector implements SliceVisitor {
	private HashMap<String, BreakPoint> bkpMap = new HashMap<String, BreakPoint>();
	private BreakPoint curBkp;
	private IVariableCollectorContext instContext = NullVariableContext.getInstance();
	private IBreakpointCustomizer bkpCustomizer = null;
	private boolean finish = false;
	
	public void reset() {
		bkpMap.clear();
	}
	
	private void add(InstructionInstance from, InstructionInstance to, Variable variable) {
		Instruction toInstr = to.getInstruction();
		if (isAccepted(toInstr)) {
			String locId = InstructionUtils.getLocationId(toInstr);
			BreakPoint bkp = null;
			if (curBkp != null && curBkp.getId().equals(locId)) {
				bkp = curBkp;
			} else {
				bkp = bkpMap.get(locId);
				if (bkp == null) {
					bkp = InstructionUtils.getBreakpoint(toInstr);
					bkpMap.put(locId, bkp);
				}
				submitVariables(instContext, curBkp);
				instContext.startContext(bkp.getId());
			}
			instContext.addLink(from, to, variable);
			curBkp = bkp;
		}
	}

	private void submitVariables(IVariableCollectorContext instContext, BreakPoint curBkp) {
		if (curBkp != null && !instContext.getVariables().isEmpty()) {
			curBkp.addVars(instContext.getVariables());
			instContext.endContext();
		}
	}

	protected boolean isAccepted(Instruction instruction) {
		return true;
	}
	
	@Override
	public final void visitMatchedInstance(InstructionInstance instance) {
		Instruction instruction = instance.getInstruction();
		if (isAccepted(instruction)) {
			String locId = InstructionUtils.getLocationId(instruction);
			if (bkpMap.get(locId) == null) {
				bkpMap.put(locId, InstructionUtils.getBreakpoint(instruction));
			}
		}
	}

	@Override
	public final void visitSliceDependence(InstructionInstance from,
			InstructionInstance to, de.unisb.cs.st.javaslicer.variables.Variable variable, int distance) {
		add(from, to, variable);
	}

	public List<BreakPoint> getDynamicSlice() {
		if (!finish) {
			onFinish();
		}
		return new ArrayList<BreakPoint>(bkpMap.values());
	}
	
	private void onFinish() {
		submitVariables(instContext, curBkp);
		if (bkpCustomizer != null) {
			bkpCustomizer.customize(bkpMap);
		}
		finish = true;
	}

	public void setVariableCollectorContext(IVariableCollectorContext context) {
		this.instContext = context;
	}
	
	public void setBkpCustomizer(IBreakpointCustomizer bkpCustomizer) {
		this.bkpCustomizer = bkpCustomizer;
	}
}
