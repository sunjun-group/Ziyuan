/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer.instruction.variable.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;
import slicer.javaslicer.IVariableCollectorContext;
import slicer.javaslicer.instruction.variable.InstVariableContext;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstance;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionInstanceInfo;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.variables.Variable;

/**
 * @author LLT, commented by Yun Lin.
 * <br>
 * This class maintains the context for a certain breakpoint. The context of a breakpoint includes
 * root instructions and their input instructions. We may consider the input instructions of a certain
 * instruction, I, as some instructions pointing to I through data flow (actually, it is more than just
 * data flow). 
 * <br>
 * <br>
 * The aim of maintaining such a context lies in that we need to analyze the context (i.e., input) when 
 * interpreting instruction variables into source code variable. For example, we may get an instruction
 * as IASTORE[1000, 1] which means that there is an array access with array ID of 1000 and array index of 1.
 * As we need to map this array to a certain variable in source code, we may need to check its input such
 * as GETFIELD a, PUSH 1, etc., so that we may know the fact.  
 *
 */
public class InstructionContext implements IVariableCollectorContext, ITreeContext {
	/**
	 * the context is for a certain location
	 */
	private String locId;
	private Map<Instruction, InstructionNode> instrMap;

	private Set<InstructionNode> varTreeRoots;
	private Map<InstructionType, InstructionHandler> instrHandlers;
	private InstVariableContext instVarContext;
	
	public InstructionContext() {
		instrMap = new HashMap<Instruction, InstructionNode>();
		varTreeRoots = new HashSet<InstructionNode>();
		instrHandlers = new HashMap<InstructionType, InstructionHandler>();
		instVarContext = new InstVariableContext();
	}
	
	@Override
	public void startContext(String locId) {
		this.locId = locId;
	}
	
	@Override
	public void endContext() {
		instrMap.clear();
		varTreeRoots.clear();
	}
	
	/**
	 * 1) build dependency instruction tree.
	 * 2) add the root nodes if from is null or fromNode is null.
	 */
	public void addLink(InstructionInstance from, InstructionInstance to,
			Variable variable) {
		boolean toInstExistInMap = true;
		InstructionNode toInstrNode = instrMap.get(to.getInstruction());
		if (toInstrNode == null) {
			toInstExistInMap = false;
			toInstrNode = new InstructionNode(to, getInstructionHandler(to));
		}
		/* check whether from already exist, if not -> add a new root */
		/**
		 * Yun Lin: 
		 * I think this implies that a from-node usually will have been visited before a to-node.
		 * If a from-node does not exist, it means the from-node may not be an interesting instruction
		 * for us.
		 */
		if(from == null || instrMap.get(from.getInstruction()) == null){
			addRoot(toInstrNode);
		}
		else{
			InstructionNode fromInstNode = instrMap.get(from.getInstruction());
			/**
			 * when the variable is not an instance of @{code StackEntry), fromInstNode and toInstrNode will
			 * not be linked. I just re-write Lyly's code to make it more understandable. However, I now (2015/12/08)
			 * still do not quite understand why to use @{code StackEntry) to check link @{code from} to @{code to}.
			 */
			boolean isLinked = fromInstNode.addLink(toInstrNode, variable);
			if(isLinked && !toInstExistInMap){
				instrMap.put(toInstrNode.getInstruction(), toInstrNode);
			}
		}
		
//		InstructionNode fromInstNode;
//		if (from != null && 
//				(fromInstNode = instrMap.get(from.getInstruction())) != null) {
//			if (fromInstNode.addLink(toInstrNode, variable) && !toInstExistInMap) {
//				instrMap.put(toInstrNode.getInstruction(), toInstrNode);
//			}
//		} else {
//			addRoot(toInstrNode);
//		}
	}

	@Override
	public void addRoot(InstructionNode instrNode) {
		if (CollectionUtils.existIn(instrNode.getInstruction().getType(), InstructionType.ARRAY,
				InstructionType.FIELD, InstructionType.VAR, InstructionType.IINC)) {
			varTreeRoots.add(instrNode);
			if (!instrMap.containsKey(instrNode.getInstruction())) {
				instrMap.put(instrNode.getInstruction(), instrNode);
			}
		}
	}

	private InstructionHandler getInstructionHandler(InstructionInstance to) {
		InstructionType type = to.getInstruction().getType();
		/**
		 * the instructions of the same type share a common handler.
		 */
		InstructionHandler handler = instrHandlers.get(type);
		if (handler == null) {
			handler = InstructionHandler.createHandler(type);
			instrHandlers.put(type, handler);
		}
		handler.setTreeContext(this);
		return handler;
	}

	@Override
	public List<BreakPoint.Variable> getVariables() {
		instVarContext.startContext();
		for (InstructionNode root : varTreeRoots) {
			if(root.getInput() != null){
				System.currentTimeMillis();
			}
			
			traverseTree(root);
		}
		List<BreakPoint.Variable> bkpVars = instVarContext.getVariables();
		instVarContext.endContext();
		return bkpVars;
	}
	
	private void traverseTree(InstructionNode node) {
		Instruction instruction = node.getInstruction();
		InstructionInstanceInfo info = node.getAdditionalInfo();
		
		if(info != null){
			System.currentTimeMillis();
		}
		
		instVarContext.accessInstruction(instruction, info);
		List<InstructionNode> inputNodes = node.getTraverseNodes();
		for (InstructionNode input : inputNodes) {
			traverseTree(input);
		}
	}
	
	/* for debug */
	protected static List<Instruction> getInstructions(Instruction instruction, int line) {
		List<Instruction> result = new ArrayList<Instruction>();
		for (Instruction instr : instruction.getMethod().getInstructions()) {
			if (instr.getLineNumber() == line) {
				result.add(instr);
			}
		}
		return result;
	}
	
	public void setInstVarContext(InstVariableContext instVarContext) {
		this.instVarContext = instVarContext;
	}
	
	public String getLocId() {
		return locId;
	}
}
