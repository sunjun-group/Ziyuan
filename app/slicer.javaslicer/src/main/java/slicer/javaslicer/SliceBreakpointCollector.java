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

import sav.strategies.common.IBreakpointCustomizer;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import slicer.javaslicer.instruction.variable.DefaultVariableCollector;
import slicer.javaslicer.instruction.variable.InstructionUtils;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.slicing.SliceVisitor;
import de.unisb.cs.st.javaslicer.variables.ArrayElement;
import de.unisb.cs.st.javaslicer.variables.LocalVariable;
import de.unisb.cs.st.javaslicer.variables.ObjectField;
import de.unisb.cs.st.javaslicer.variables.StackEntry;
import de.unisb.cs.st.javaslicer.variables.StaticField;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT
 * @author Yun Lin (modified)
 *
 */
public class SliceBreakpointCollector implements SliceVisitor {
	private HashMap<String, BreakPoint> bkpMap = new HashMap<String, BreakPoint>();
	private BreakPoint curBkp;
	private IVariableCollectorContext instContext = DefaultVariableCollector.getInstance();
	private IBreakpointCustomizer bkpCustomizer = null;
	private boolean finish = false;	
	
	public void reset() {
		bkpMap.clear();
		finish();
		finish = false;
	}
	
	private void add(InstructionInstance from, InstructionInstance to, Variable variable) {
		
		Instruction toInstr = to.getInstruction();
		
		if (isAccepted(toInstr)) {
			
			String locId = InstructionUtils.getLocationId(toInstr);
			if(locId.contains("12")){
				System.currentTimeMillis();
			}
			
			buildRWRelations(from, to, variable);
			
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
			
			if(bkp != null && bkp.getLineNo() == 12){
				System.currentTimeMillis();
			}
			
			instContext.addLink(from, to, variable);
			curBkp = bkp;
		}
	}
	
	/**
	 * note that given a node @{code from} which data-depends on a node @{code to}, @{code from} should
	 * read the variable while @{code to} should write the variable.
	 */
	private void buildRWRelations(InstructionInstance from, InstructionInstance to, Variable variable) {
		if(variable == null){
			return;
		}
		
		/**
		 * construct break points corresponding to @{code from} and @{code to}
		 */
		String fromLoc = InstructionUtils.getLocationId(from);
		String toLoc = InstructionUtils.getLocationId(to);

		BreakPoint fromBP = bkpMap.get(fromLoc);
		if(fromBP == null){
			fromBP = InstructionUtils.getBreakpoint(from.getInstruction());
			bkpMap.put(fromLoc, fromBP);
		}
		BreakPoint toBP = bkpMap.get(toLoc);
		if(toBP == null){
			toBP = InstructionUtils.getBreakpoint(to.getInstruction());
			bkpMap.put(toLoc, toBP);
		}
		
		sav.strategies.dto.BreakPoint.Variable var = convertVar(variable);
		if(var == null){
			//System.err.println("some cases unhandled in convertVar() method");
		}
		else{
			fromBP.addReadVariable(var);
			toBP.addWrittenVariable(var);			
		}
		
	}

	private sav.strategies.dto.BreakPoint.Variable convertVar(Variable variable) {
		sav.strategies.dto.BreakPoint.Variable var = null;
		if(variable instanceof LocalVariable){
			LocalVariable lv = (LocalVariable)variable;
			String fullName = lv.getVarName();
			var = new sav.strategies.dto.BreakPoint.Variable(fullName, fullName, VarScope.UNDEFINED);
		}
		else if(variable instanceof ObjectField){
			ObjectField field = (ObjectField)variable;
			String fieldName = field.getFieldName();
			var = new sav.strategies.dto.BreakPoint.Variable(fieldName, fieldName, VarScope.THIS);
		}
		else if(variable instanceof StaticField){
			StaticField sv = (StaticField)variable;
			String fieldName = sv.getFieldName();
			var = new sav.strategies.dto.BreakPoint.Variable(fieldName, fieldName, VarScope.STATIC);
		}
		else if(variable instanceof StackEntry){
			/**
			 * do nothing
			 */
		}
		else if(variable instanceof ArrayElement){
			ArrayElement ae = (ArrayElement)variable;
			int arrayIndex = ae.getArrayIndex();
			long arrayId = ae.getArrayId();
			
			//TODO
			
			System.currentTimeMillis();
		}
		
		return var;
	}

	private void submitVariables(IVariableCollectorContext instContext, BreakPoint curBkp) {
		if (curBkp != null) {
			
			if(curBkp.getLineNo() == 12){
				System.currentTimeMillis();
			}
			
			List<sav.strategies.dto.BreakPoint.Variable> variables = instContext.getVariables(); 
			if(!variables.isEmpty()){
				curBkp.addVars(variables);
				instContext.endContext();
			}
		}
	}

	protected boolean isAccepted(Instruction instruction) {
		return true;
	}
	
	@Override
	public final void visitMatchedInstance(InstructionInstance instance) {
		add(null, instance, null);
	}

	@Override
	public final void visitSliceDependence(InstructionInstance from,
			InstructionInstance to, de.unisb.cs.st.javaslicer.variables.Variable variable, int distance) {
		
		add(from, to, variable);
	}

	public List<BreakPoint> getDynamicSlice() {
		finish();
		return new ArrayList<BreakPoint>(bkpMap.values());
	}
	
	private void finish() {
		if (finish) {
			return;
		}
		/* finish */
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
